package controllers;

import java.io.ByteArrayInputStream;

import com.google.protobuf.AbstractMessageLite;
import play.mvc.Controller;
import play.mvc.results.RenderBinary;

public class PBActions {
    public static void render(final AbstractMessageLite message) {
        throw new RenderBinary(
            new ByteArrayInputStream(message.toByteArray()),
            null,
            0,
            "application/x-google-protobuf",
            true
        );
    }
}
