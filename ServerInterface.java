import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

	User login(String username, String password)
			throws RemoteException, GameException.ClientException, GameException.ServerException;

	User register(String username, String password)
			throws RemoteException, GameException.ClientException, GameException.ServerException;

	void logout(User user) throws RemoteException, GameException.ServerException;

	ArrayList<User> getAllUsers() throws RemoteException, GameException.ServerException;
}
