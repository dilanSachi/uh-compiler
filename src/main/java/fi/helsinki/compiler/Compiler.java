package fi.helsinki.compiler;

import fi.helsinki.compiler.assemblygen.Assembler;
import fi.helsinki.compiler.assemblygen.AssemblyGenerator;
import fi.helsinki.compiler.common.CommonStatics;
import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.exceptions.CompilationException;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.irgenerator.IRGenerator;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.typechecker.TypeChecker;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Compiler {
    public static void main(String[] args) throws Exception {
        String command = null;
        String inputFile = null;
        String outputFile = null;
        String host = "0.0.0.0";
        int port = 3000;
        for (String arg : args) {
            if (arg.startsWith("--output=")) {
                outputFile = arg.substring(9);
            } else if (arg.startsWith("--host=")) {
                host = arg.substring(7);
            } else if (arg.startsWith("--port=")) {
                port = Integer.valueOf(arg.substring(7));
            } else if (arg.startsWith("-")) {
                throw new CompilationException("Unknown argument: "  + arg);
            } else if (command == null) {
                command = arg;
            } else if (inputFile == null) {
                inputFile = arg;
            } else {
                throw new CompilationException("Multiple input files not supported");
            }
        }
        if (command == null) {
            throw new CompilationException("Command argument missing");
        }
        if (command.equals("compile")) {
            String sourceCode = readSourceCode(inputFile);
            if (outputFile == null) {
                throw new CompilationException("Output file flag --output=... required");
            }
            startCompilation(sourceCode, outputFile);
        } else if (command.equals("serve")) {
            CompilerServer compilerServer = new CompilerServer(host, port);
            compilerServer.startServer();
        } else {
            throw new CompilationException("Invalid compilation command: " + command);
        }
    }

    private static class CompilerServer {
        private int port;
        private String host;

        public CompilerServer(String host, int port) {
            this.port = port;
            this.host = host;
        }

        public void startServer() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server started. Waiting for a connection...");
                while (true) {
                    Thread handlerThread = new Thread(new ServerHandler(serverSocket.accept()));
                    handlerThread.start();
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }
    }

    private static class ServerHandler implements Runnable {
        Socket clientSocket;

        public ServerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                StringBuilder requestBuilder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        requestBuilder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonRequest = requestBuilder.toString();
                System.out.println("Received JSON request: " + jsonRequest);
                String response = "";
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonRequest);
                    if (jsonObject.get("command").equals("compile")) {
                        String sourceCode = jsonObject.get("code").toString();
                        byte[] byteCode = startCompilation(sourceCode, null);
                        Map<String, Object> map = new HashMap<>();
                        map.put("program", new String(Base64.getEncoder().encode(byteCode)));
                        response = new JSONObject(map).toJSONString();
                    }
                } catch (Exception e) {
                    response = "{\"error\": \"Error occurred while compiling: " + e.getMessage() + "\"}";
                    System.out.println("Error occurred while compiling: " + e.getMessage());
                }
                try {
                    writer.write(response);
                    writer.newLine();
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Response sent. Closing connection.");
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
            }
        }
    }

    private static byte[] startCompilation(String sourceCode, String output) throws ParserException,
            TypeCheckerException, IRGenerationException, ClassNotFoundException, IllegalAccessException,
            IOException, InterruptedException {
        Parser parser = new Parser(new Tokenizer().tokenize(sourceCode, "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        typeChecker.checkType(expression);
        List<Instruction> instructions = new IRGenerator(new CommonStatics()).generateIR(expression);
        if (output == null) {
            return new Assembler().assembleAndGetExecutable(new AssemblyGenerator().generateAssembly(instructions),
                    null, "tempfile", true, new String[]{});
        } else {
            new Assembler().assemble(new AssemblyGenerator().generateAssembly(instructions), output,
                    null, "tempfile", true, new String[]{});
            return null;
        }
    }

    private static String readSourceCode(String inputFilePath) throws IOException {
        if (inputFilePath != null) {
            return Files.readString(Path.of(inputFilePath));
        } else {
            Scanner stdScanner = new Scanner(System.in);
            return stdScanner.next();
        }
    }
}
