package com.example.javafxdemo;

import java.io.*;

public class FileReaderSaver<T> implements Saver<T>, Reader<T>{
    private final String filename;

    public FileReaderSaver(String filename) {
        this.filename = filename;
    }

    @Override
    public void save(T value) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(value);

            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (IOException e) {
            System.out.println("Problem with saving data to file");
        }
    }

    @Override
    public T read() {
        T values = null;

        try {

            FileInputStream fileInputStream = new FileInputStream(filename);

            if (fileInputStream.available() > 0) {                       //checking if file isn't empty

                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                values = (T) objectInputStream.readObject();

                objectInputStream.close();
            }
        } catch (EOFException e) {
            System.out.println("End of file lol");
        } catch (IOException e) {
            System.out.println("Problem with getting values from file");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found exception");
        }

        return values;
    }
}
