import java.io.*;
import java.util.*;
import java.math.BigInteger;


public class INFO implements Serializable {

	private String sender;
	private String reciever;
	private Serializable data = null;

	public String getReciever() {
		return reciever;
	}

	public String getSender() {
		return sender;
	}

	public Serializable getData() {
		return data;
	}

	public INFO(String sender, String reciever, Serializable data) {
		this.sender = sender;
		this.reciever = reciever;
		this.data = data;
	}

	@Override
	public String toString() {
		return reciever + " <-- " + sender  + " :: [ " + data + " ]";
	}
}

