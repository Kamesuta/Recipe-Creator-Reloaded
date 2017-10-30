package com.fiscalleti.recipecreator.serialization;

import java.io.*;

/**
 * User: Nathan Fiscaletti
 * Date: 4/9/13
 * Time: 9:29 AM
 * http://www.fiscalleti.com/
 */
public class ObjectHandler {
    public static void write(Object c, String file) throws IOException {
        OutputStream fi = new FileOutputStream(file);
        OutputStream buffer = new BufferedOutputStream(fi);
        ObjectOutput output = new ObjectOutputStream(buffer);
        try{
            output.writeObject(c);
        }finally{
            output.close();
            buffer.close();
            fi.close();
        }
    }
    public static Object read(String file) throws IOException, FileNotFoundException, ClassNotFoundException{
        InputStream fi = new FileInputStream( file );
        InputStream buffer = new BufferedInputStream( fi );
        ObjectInput input = new ObjectInputStream( buffer );
        Object ret = input.readObject();
        input.close();
        buffer.close();
        fi.close();
        return ret;
    }
}
