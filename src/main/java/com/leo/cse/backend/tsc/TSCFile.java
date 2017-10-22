package com.leo.cse.backend.tsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.exe.ExeData;

public class TSCFile {

	private String script;

	public TSCFile(File in) throws IOException {
		FileInputStream fileIn = new FileInputStream(in);
		FileChannel inChan = fileIn.getChannel();
		ByteBuffer dataBuf = null;
		int fileSize = 0;
		fileSize = (int) inChan.size();
		dataBuf = ByteBuffer.allocate(fileSize);
		inChan.read(dataBuf);
		fileIn.close();
		byte[] datArray = null;
		if (fileSize > 0) {
			int cypher = dataBuf.get(fileSize / 2);
			datArray = dataBuf.array();
			if (in.getName().endsWith(".tsc")) {
				for (int i = 0; i < fileSize; i++) {
					if (i != fileSize / 2) {
						datArray[i] -= cypher;
					}
				}
			}
		}
		// now read the input as text
		script = StrTools.ENCODING_ERROR;
		if (datArray != null)
			script = StrTools.CString(datArray, ExeData.getEncoding());
	}
	
	public String getScript() {
		return script;
	}

}
