package bham.bioshock.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLReader {
  private Document document;

  public XMLReader(String path) {
    try {
      File xmlFile = new File(path);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      document = dBuilder.parse(xmlFile);
      document.getDocumentElement().normalize();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void printNodes(String tag) {
    if (document == null) {
      System.out.println("Document is Null");
    } else {
      NodeList nList = document.getElementsByTagName(tag);

      for (int i = 0; i < nList.getLength(); i++) {
        Node node = nList.item(i);
        System.out.println("\nCurrent Element :" + node.getNodeName());
      }
    }
  }

  public String getTag(String tag) {
    if (document == null) {
      System.out.println("Document is Null");
      return null;
    } else {
      NodeList nList = document.getElementsByTagName(tag);

      for (int i = 0; i < nList.getLength(); i++) {
        Node node = nList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;

          String content = element.getTextContent();
          return content;
        }
      }
    }
    return "";
  }

  public int getInt(String tag) {
    if (document == null) {
      System.out.println("Document is Null");
      return -1;
    } else {
      NodeList nList = document.getElementsByTagName(tag);
      if (nList == null) {
        System.out.println("No such Tag");
        return -1;
      } else {
        Node node = nList.item(0);
        String content = node.getTextContent().trim();
        return Integer.parseInt(content);
      }
    }
  }
}
