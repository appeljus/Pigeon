package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;

public class SendFile implements Runnable {
	Client c;
	File file;
	InetAddress target;
	int port;
	byte[] fileParts;
	String ext;
	String name;

	int currentStartI = 0;

	public SendFile(Client c, File f, InetAddress target, int port) {
		this.c = c;
		file = f;
		this.target = target;
		this.port = port;

		String pathString = file.getPath();
		Path path = Paths.get(pathString);
		try {
			fileParts = Files.readAllBytes(path);
		} catch (IOException e) {
			System.out.println("Fuck the File!");
		}

		ext = FilenameUtils.getExtension(pathString);
		System.out.println(ext.toString() + " | " + ext);

	}

	@Override
	public void run() {
		byte[] data = new byte[1003];
		byte[] tagData = new byte[11];
		int j = 0;
		while (currentStartI < fileParts.length) {
			int i;
			for (i = 0; i < 1003 && currentStartI + i < fileParts.length; i++) {
				data[i] = fileParts[currentStartI + i];
			}
			if (currentStartI < fileParts.length) {
				tagData = "[FILE]".getBytes();
			}
			j = i+1;
			currentStartI += i;
			byte[] data3 = new byte[1014];
			System.arraycopy(tagData, 0, data3, 0, tagData.length);
			System.arraycopy(data, 0, data3, tagData.length, data.length);
			//System.out.println(new String(data3));
			
			byte[] data4 = PacketUtils.getData(data3, c.getCurrentSeq(), c.getHopCount(), c.getMyAddress(), c.getGroup());
			DatagramPacket packetToSend = new DatagramPacket(data4, data4.length, target, port);
			
			c.resendPacket(packetToSend);
			c.incrementSeqNr();
		}
		if(ext.length() == 3) tagData = ("[EOF][" + ext + "] ").getBytes();
		else tagData = ("[EOF][" + ext + "]").getBytes();
		
		System.out.println(tagData.length);
		
		byte[] data3 = new byte[1014];
		data[j] = (byte) 255;
		
		System.out.println(data.length);
		
		System.arraycopy(tagData, 0, data3, 0, tagData.length);
		System.arraycopy(data, 0, data3, tagData.length, data.length);
		//System.arraycopy(filler, 0, data3, tagData.length + data.length, filler.length);
		
		//System.out.println(new String(data3));
		byte[] data4 = PacketUtils.getData(data3, c.getCurrentSeq(), c.getHopCount(), c.getMyAddress(), c.getGroup());
		DatagramPacket packetToSend = new DatagramPacket(data4, data4.length, target, port);
		
		c.resendPacket(packetToSend);
		c.incrementSeqNr();
	}

}
