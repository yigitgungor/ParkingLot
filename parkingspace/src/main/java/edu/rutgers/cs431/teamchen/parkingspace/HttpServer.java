public class HttpServer extends Thread{

    //Private variables
    private Socket connectedClient = null;
    private BufferedReader clientRequest = null;
    private DataOutputStream responseToClient = null;

    /**
     * Public constructor
     * @param client
     */
    public HttpServer(Socket client){
        connectedClient = client;
    }

    /**
     * Code to execute on thread
     */
    public void run(){

        try {

            //Log new client
            System.out.println("The client " + connectedClient.getInetAddress() + 
                    ":" + connectedClient.getPort() + " is connected");

            //Get the client request
            clientRequest = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));

            //Start response object
            responseToClient = new DataOutputStream(connectedClient.getOutputStream());

            //Process the request
            processClientRequest();

            //Close buffered writer
            responseToClient.close();
        } catch (Exception e) {

            //Print error
            e.printStackTrace();
        }
    }

    /**
     * Parses a client request and calls the approriate handler
     * @throws Exception
     */
    private void processClientRequest() throws Exception{

        String requestString = clientRequest.readLine();

        String header = requestString;

        //Break up request
        StringTokenizer tokenizer = new StringTokenizer(header);

        //Different request parts
        String httpMethod = tokenizer.nextToken();
        String httpQueryString = tokenizer.nextToken();

        //Print client request
        StringBuffer responseBuffer = new StringBuffer();
        while (clientRequest.ready()) {
            responseBuffer.append(requestString + " ");
            System.out.println(requestString);

            requestString = clientRequest.readLine();
        }
        //ID GET request
        if (requestString.equals("GET")) {
            if (httpQueryString.equals("/")) {
                sendResponse();

            }   
        }
    }

    /**
     * Sends reply back to client
     * @throws Exception
     */
    private void sendResponse() throws Exception{
      

        Gson gson = new Gson();
        String json = gson.toJson(mapResponse); 
        new PrintWriter(responseToClient, true).println(json);
    }
}