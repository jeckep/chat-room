package com.jeckep.chat.util;

import org.apache.velocity.Template;
import org.apache.velocity.app.*;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.eclipse.jetty.http.HttpStatus;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.*;

import java.util.HashMap;
import java.util.Map;

import static com.jeckep.chat.util.RequestUtil.*;

public class ViewUtil {

    // Renders a template given a model and a request
    // The request is needed to check the user session for language settings
    // and to see if the user is logged in
    public static String render(Request request, Map<String, Object> model, String templatePath) {
        model.put("msg", new MessageBundle(getSessionLocale(request)));
        model.put("currentUser", getSessionCurrentUser(request));
        model.put("WebPath", Path.Web.class); // Access application URLs from templates
        if(!model.containsKey("nav_active")){
            model.put("nav_active", "");
        }
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }


    public static Route notFound = (Request request, Response response) -> {
        response.status(HttpStatus.NOT_FOUND_404);
        return render(request, new HashMap<>(), Path.Template.NOT_FOUND);
    };

    private static VelocityTemplateEngine engine;
    private static VelocityTemplateEngine strictVelocityEngine() {
        if(engine == null) {
            VelocityEngine configuredEngine = new VelocityEngine() {
                @Override
                public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException {
                    return super.getTemplate(name, "UTF-8");
                }
            };
            configuredEngine.setProperty("runtime.references.strict", true);
            configuredEngine.setProperty(RuntimeConstants.PARSER_POOL_SIZE, 100);
            configuredEngine.setProperty(RuntimeConstants.VM_LIBRARY_AUTORELOAD, false);
            configuredEngine.setProperty("resource.loader", "class");
            configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            engine = new VelocityTemplateEngine(configuredEngine);
        }
        return engine;
    }
}
