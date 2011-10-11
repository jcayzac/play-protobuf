package play.modules.protobuf;

import java.io.ByteArrayInputStream ;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import org.apache.commons.io.IOUtils;
import play.data.binding.Binder;
import play.data.binding.TypeBinder;
import play.data.parsing.DataParser;
import play.exceptions.UnexpectedException;
import play.jobs.*;
import play.mvc.Http.Request;
import play.mvc.results.RenderBinary;
import play.PlayPlugin;

public class ProtobufPlugin extends PlayPlugin {
	private static final String MAGIC_ARGUMENT_NAME = "ProtobufPlugin.Body";
	private static Map<String, String[]> FAKE_PARAMS = new HashMap<String, String[]>();

	static {
		FAKE_PARAMS.put("request", new String[] {""});
	}

	@Override
	public void onApplicationStart() {
		DataParser.parsers.put(
			"application/x-google-protobuf",
			new DataParser() {
				@Override
				public Map<String, String[]> parse(InputStream is) {
					try {
						Request.current().args.put(
							MAGIC_ARGUMENT_NAME,
							ByteString.copyFrom(IOUtils.toByteArray(is))
						);
						return FAKE_PARAMS;
					} catch (Exception e) {
						throw new UnexpectedException(e);
					}
				}
			}
		);

		Binder.register(AbstractMessageLite.class, new TypeBinder<AbstractMessageLite>() {
			@Override
			public Object bind(
				String name,
				Annotation[] annotations,
				String value,
				Class actualClass,
				Type genericType
			) throws Exception {
				@SuppressWarnings("unchecked")
				Method m = actualClass.getMethod("parseFrom", new Class[] {ByteString.class});
				return m.invoke(null, new Object[] {
					(ByteString) Request.current().args.get(MAGIC_ARGUMENT_NAME)
				});
			}
		});
	}
}
