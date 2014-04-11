package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.*;

public class SendFile implements Runnable{
	Client c;
	File file;
	InetAddress target;
	int port;
	byte[] fileParts;
	String ext;
	String name;
	
	int currentStartI = 0;
	
	public SendFile(Client c, File f, InetAddress target, int port){
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
		name = FilenameUtils.getName(pathString);
		
	}

	@Override
	public void run() {
		while(currentStartI < fileParts.length){
			byte[] data = new byte[1003];
			int i;
			for(i = 0; i < 1003 && currentStartI + i < fileParts.length; i++){
				data[i] = fileParts[currentStartI + i];
			}
			currentStartI += i;
			byte[] data2 = PacketUtils.getData(data, c.getCurrentSeq(), c.getHopCount(), c.getMyAddress(), c.getGroup());
			DatagramPacket packetToSend = new DatagramPacket(data, data.length, target, port);
			
		
		
		c.incrementSeqNr();
		}
	}
	
}