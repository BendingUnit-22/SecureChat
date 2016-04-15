import java.io.*;
import java.util.*;
import java.math.BigInteger;


public class INFO implements Serializable {

	private String sender;
	private Serializable data = null;
	private  boolean local;


	public String getSender() {
		return sender;
	}

	public Serializable getData() {
		return data;
	}

	public INFO(String sender, Serializable data, boolean localCopy) {
		this.sender = sender;
		this.data = data;
		local = localCopy;
	}

	public boolean isLocalCopy(){
		return local;
	}


	@Override
	public String toString() {
		return "me" + " <-- " + sender  + " :: [ " + data + " ]";
	}
}
