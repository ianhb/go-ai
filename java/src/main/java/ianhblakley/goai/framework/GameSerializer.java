package ianhblakley.goai.framework;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes and writes game logs to files.
 *
 * Created by ian on 10/14/16.
 */
@SuppressWarnings("ALL")
class GameSerializer {

    /**
     * Writes the game to a file
     *
     * @param game     game to write
     * @param filename name of file to write to
     * @throws IOException thrown when unable to write
     */
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

    /**
     * Writes all of the games in games to a single file
     * @param games games to write
     * @param filename name of file to write to
     * @throws IOException thrown when unable to write
     */
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

    /**
     * Reads all of the games from filename and returns a list of the games read
     * @param filename name of file to read from
     * @return list of games in filename
     * @throws IOException thrown when unable to read
     * @throws ClassNotFoundException thrown when read contents aren't {@link Game}
     */
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

    /**
     * Written to end of each file to allow readback
     */
    private static class EndOfFile implements Serializable {}
}
