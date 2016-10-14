package ianhblakley.goai.framework;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes and writes game logs to files.
 *
 * Created by ian on 10/14/16.
 */
public class GameSerializer {

    public void serialize(Game game, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(game);
        oos.writeObject(new EndOfFile());
        oos.close();
        bos.close();
        fos.close();
    }

    public void serialize(List<Game> games, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        for (Game g : games) {
            oos.writeObject(g);
        }
        oos.writeObject(new EndOfFile());
        oos.close();
        bos.close();
        fos.close();
    }

    public List<Game> deserialize(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        List<Game> games = new ArrayList<>();
        while (true) {
            Object obj = ois.readObject();
            if (obj instanceof EndOfFile) {
                break;
            }
            games.add((Game) obj);
        }
        ois.close();
        bis.close();
        fis.close();
        return games;
    }

    private static class EndOfFile implements Serializable {}
}
