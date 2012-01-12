package com.atex.confluence.plugin.nexus;

import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pau
 */
public class ConfigurationServlet extends HttpServlet {

    private final TemplateRenderer renderer;
    
    
    public ConfigurationServlet(TemplateRenderer renderer) {
        this.renderer = renderer;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        log(new Date().toString());
        resp.setContentType("text/html;charset=utf-8");
        renderer.render("configure.vm", resp.getWriter());
    }
    
    
    
}
