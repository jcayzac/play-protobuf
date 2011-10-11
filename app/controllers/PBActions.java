package controllers;

import java.io.ByteArrayInputStream ;

import com.google.protobuf.AbstractMessageLite;
import play.mvc.Controller;
import play.mvc.results.RenderBinary;

public class PBActions {
	public static void render(AbstractMessageLite message) {
		byte[] bytes = message.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        throw new RenderBinary(inputStream, null, 0, "application/x-google-protobuf", true);
    }
}
