package bham.bioshock.client;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLInteraction {

  /**
   * Method to read the preferences from an XML file and return them as an AppPreferences object
   *
   * @return The AppPreferences object with values from the XML file
   */
  public AppPreferences XMLtoPreferences() {

    boolean musicEnabled = false;
    float musicVolume = 0f;
    boolean soundsEnabled = false;
    float soundsVolume = 0f;


    File XMLfile = new File("app/assets/Preferences/Preferences.XML");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;

    // get the XML file as a document in memory
    try {
      documentBuilder = dbFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(XMLfile);
      document.getDocumentElement().normalize();
      NodeList nodeList = document.getElementsByTagName("sound");

      // go through all the nodes in the document
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);

        // if we have found the element node we want
        if (node.getNodeType() == Node.ELEMENT_NODE) {

          // get the node as an element
          Element element = (Element) node;

          // get the strings stored in the document
          String musicEnabledString = element.getElementsByTagName("music_enabled").item(0)
              .getTextContent();
          String musicVolumeString = element.getElementsByTagName("music_volume").item(0)
              .getTextContent();
          String soundsEnabledString = element.getElementsByTagName("sounds_enabled").item(0)
              .getTextContent();
          String soundsVolumeString = element.getElementsByTagName("sounds_volume").item(0)
              .getTextContent();

          // turn the strings into the types we want
          if (musicEnabledString.equals("1")){
            musicEnabled = true;
          }else{
            musicEnabled = false;
          }
          musicVolume = (float) (Integer.parseInt(musicVolumeString) / 100);

          if (soundsEnabledString.equals("1")){
            soundsEnabled = true;
          }else{
            soundsEnabled = false;
          }
          soundsVolume = (float) (Integer.parseInt(soundsVolumeString) / 100);
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return new AppPreferences(musicEnabled, musicVolume, soundsEnabled, soundsVolume);
  }
}
