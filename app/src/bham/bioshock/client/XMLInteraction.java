package bham.bioshock.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;


/**
 * The type XMLInteraction.
 */
public class XMLInteraction {

  private static final Logger logger = LogManager.getLogger(XMLInteraction.class);
  
  private static final String PREFERENCES_PATH = "app/assets/Preferences/Preferences.XML";
  private static final String GAME_DESC_PATH = "app/assets/Preferences/Preferences.XML";
  
  /**
   * Method to read the preferences from an XML file and return them as an AppPreferences object
   *
   * @return The AppPreferences object with values from the XML file
   */
  public AppPreferences xmlToPreferences() {

    boolean musicEnabled = false;
    float musicVolume = 0f;
    boolean soundsEnabled = false;
    float soundsVolume = 0f;

    File xmlFile = new File(PREFERENCES_PATH);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;

    try {
      // get the XML file as a document in memory
      documentBuilder = dbFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(xmlFile);
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
          if (musicEnabledString.equals("1")) {
            musicEnabled = true;
          } else {
            musicEnabled = false;
          }
          musicVolume = Float.parseFloat(musicVolumeString) / 100;

          if (soundsEnabledString.equals("1")) {
            soundsEnabled = true;
          } else {
            soundsEnabled = false;
          }
          soundsVolume = Float.parseFloat(soundsVolumeString) / 100;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    logger.debug("XML file read successfully");
    return new AppPreferences(musicEnabled, musicVolume, soundsEnabled, soundsVolume);
  }

  /**
   * Method to write the new preferences to the file once changes are complete
   *
   * @param musicEnabled Whether music should be saved as enabled or not
   * @param musicVolume The music volume that should be saved
   * @param soundsEnabled Whether sounds should be saved as enabled or not
   * @param soundsVolume The sounds volume that should be saved
   */
  public void preferencesToXML(boolean musicEnabled, float musicVolume, boolean soundsEnabled,
      float soundsVolume) {
    File xmlFile = new File(PREFERENCES_PATH);

    String musicEnabledString;
    String musicVolumeString;
    String soundsEnabledString;
    String soundsVolumeString;

    if (musicEnabled) {
      musicEnabledString = "1";
    } else {
      musicEnabledString = "0";
    }
    musicVolumeString = String.valueOf((int) (musicVolume * 100));

    if (soundsEnabled) {
      soundsEnabledString = "1";
    } else {
      soundsEnabledString = "0";
    }
    soundsVolumeString = String.valueOf((int) (soundsVolume * 100));

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;

    try {
      // get the XML file as a document in memory
      documentBuilder = dbFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(xmlFile);
      document.getDocumentElement().normalize();

      // write the changes to the document
      NodeList nodeList = document.getElementsByTagName("sound");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);

        Node music_enabled = element.getElementsByTagName("music_enabled").item(0).getFirstChild();
        music_enabled.setNodeValue(musicEnabledString);

        Node music_volume = element.getElementsByTagName("music_volume").item(0).getFirstChild();
        music_volume.setNodeValue(musicVolumeString);

        Node sounds_enabled = element.getElementsByTagName("sounds_enabled").item(0)
            .getFirstChild();
        sounds_enabled.setNodeValue(soundsEnabledString);

        Node sounds_volume = element.getElementsByTagName("sounds_volume").item(0).getFirstChild();
        sounds_volume.setNodeValue(soundsVolumeString);
      }

      // write the updated document to file or console
      document.getDocumentElement().normalize();
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(new File(PREFERENCES_PATH));
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(source, result);
      logger.debug("XML file updated successfully");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method to get the game description and things needed from the game_desc file to use in the
   * HowToScreen
   *
   * @return The arraylist containing the strings from the XML file
   */
  public HashMap<String, String> xmlToDescription() {
    HashMap<String, String> readText = new HashMap<>();
    File xmlFile = new File(GAME_DESC_PATH);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;

    try {
      // get the XML file as a document in memory
      documentBuilder = dbFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(xmlFile);
      document.getDocumentElement().normalize();
      NodeList nodeList = document.getElementsByTagName("how_to");

      // go through all the nodes in the document
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);

        // if we have found the element node we want
        if (node.getNodeType() == Node.ELEMENT_NODE) {

          // get the node as an element
          Element element = (Element) node;

          // get the strings stored in the document and add to the arraylist
          String gameDescription = element.getElementsByTagName("game_desc").item(0)
              .getTextContent();
          readText.put("gameDescription", gameDescription);

          for(int j=1;j<4;j++) {
            String howToMove = element.getElementsByTagName("how_to_move_"+j).item(0)
                    .getTextContent();
            readText.put("howToMove_"+j, howToMove);
          }

          for(int j=1;j<3;j++) {
            String howToCapture = element.getElementsByTagName("how_to_capture_"+j).item(0)
                    .getTextContent();
            readText.put("howToCapture_"+j, howToCapture);
          }


          String fuel = element.getElementsByTagName("fuel").item(0)
                  .getTextContent();
          readText.put("fuel", fuel);

          String upgrade = element.getElementsByTagName("upgrade").item(0)
                  .getTextContent();
          readText.put("upgrade", upgrade);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return readText;
  }
}
