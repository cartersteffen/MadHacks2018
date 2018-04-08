//package net.codejava.servlet;

import java.io.IOException;
import java.io.PrintWriter;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;


@WebServlet("/WordServlet")
public class WordServlet extends HttpServlet {
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
         
        // read form fields
        String word = request.getParameter("word");
        String wordCloud = TwitterAPIHandler.getWordCloud(word);
        URL picture = new URL(wordCloud);
        //String password = request.getParameter("password");
         
        //System.out.println("username: " + username);
        //System.out.println("password: " + password);
 
        // do some processing here...
         
        // get response writer
        PrintWriter writer = response.getWriter();
         
        // build HTML code
        String htmlRespone = "<html>";
        htmlRespone += "<img src=" + picture + " alt='Word Cloud'>" ;
        htmlRespone += "</html>";
         
        // return response
        writer.println(htmlRespone);
         
    }
 
}