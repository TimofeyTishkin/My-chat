package Everything.task3008.client;


import Everything.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient extends Client {
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_"+(int) (Math.random()*100);
    }

    public static void main(String[] args) {
        new BotClient().run();
    }
    public class BotSocketThread extends SocketThread{
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
            if(message.contains(":")){
                String[] arr = message.split(":");
                String name = arr[0].trim();
                arr[1] = arr[1].trim();
                Calendar c = new GregorianCalendar();
                SimpleDateFormat simpleDateFormat = null;
                Date date = c.getTime();
                if((arr[1].equals("дата"))||(arr[1].equals("день"))||(arr[1].equals("месяц"))||(arr[1].equals("год"))||(arr[1].equals("время"))||(arr[1].equals("час"))||(arr[1].equals("минуты"))||(arr[1].equals("секунды"))){
                    if(arr[1].equals("дата")){
                        simpleDateFormat = new SimpleDateFormat("d.MM.YYYY");
                    }
                    else if(arr[1].equals("день")){
                        simpleDateFormat= new SimpleDateFormat("d");
                    }
                    else if(arr[1].equals("месяц")){
                        simpleDateFormat = new SimpleDateFormat("MMMM");
                    }
                    else if(arr[1].equals("год")){
                        simpleDateFormat = new SimpleDateFormat("YYYY");
                    }
                    else if(arr[1].equals("время")){
                        simpleDateFormat = new SimpleDateFormat("H:mm:ss");
                    }
                    else if(arr[1].equals("час")){
                        simpleDateFormat = new SimpleDateFormat("H");
                    }
                    else if(arr[1].equals("минуты")){
                        simpleDateFormat = new SimpleDateFormat("m");
                    }
                    else if(arr[1].equals("секунды")){
                        simpleDateFormat = new SimpleDateFormat("s");
                    }
                    sendTextMessage("Информация для "+name+": " +simpleDateFormat.format(date));}}

        }

    }
}
