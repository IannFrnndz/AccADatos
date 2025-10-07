package cuadernoC;

import java.nio.file.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.ByteBuffer;
import java.io.IOException;

public class NIOExample {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("miArchivo.txt");

        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            byte[] data = "Hola, mundo!".getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(data);
            channel.write(buffer);
        }
    }
}
