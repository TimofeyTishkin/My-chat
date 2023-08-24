package Everything.task3008.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel {
    private final Set<String> allUserNames = new HashSet<>();
    private String newMessage;

    public Set<String> getAllUserNames() {
        return Collections.unmodifiableSet(allUserNames);
    }

    public void addUser(String newUserName){
        this.allUserNames.add(newUserName);
    }

    public void deleteUser(String userName){
        this.allUserNames.remove(userName);
    }
    
    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }
}
