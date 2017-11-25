package com.example.ichwan.steganigrafivideo;

/**
 * Created by ichwan on 11/2/2017.
 */

import com.google.common.primitives.Bytes;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class Steganography {
    public static final int BERHASIL = 0;
    public static final int GAGAL = 1;
    public static final int SUDAH_ADA_DATA = 2;
    public static final int TIDAK_ADA_DATA = 3;

    private static byte[] _message ;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

//    public static int findString(byte[] in, byte[] find) {
//        boolean done = false;
//        for (int i = 0; i < in.length; i++) {
//            if (in[i] == find[0]) {
//                for (int ii = 1; ii < find.length; i++) {
//                    if (in[i + ii] != find[ii]) {
//                        break;
//                    } else if (ii == find.length - 1) {
//                        done = true;
//                    }
//                }
//                if (done) {
//                    return i - 1;
//                }
//            }
//        }
//        return -1;
//    }

    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static String panjangData(String hex) {
        String h = "";
        switch (hex.length()) {
            case 8:
                h = hex;
                break;
            case 7:
                h = "0" + hex;
                break;
            case 6:
                h = "00" + hex;
                break;
            case 5:
                h = "000" + hex;
                break;
            case 4:
                h = "0000" + hex;
                break;
            case 3:
                h = "00000" + hex;
                break;
            case 2:
                h = "000000" + hex;
                break;
            case 1:
                h = "0000000" + hex;
                break;
            default:
                throw new AssertionError();
        }
        return h;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] search(byte[] arr, byte[] sea, int length) {
        int index = Bytes.indexOf(arr, sea);
        byte[] size = Arrays.copyOfRange(arr, index - 4, index);
        int i = Integer.parseInt(Steganography.byteArrayToHex(size), 16);
        System.out.println("i:" + i);
        byte[] a = Arrays.copyOfRange(arr, (index + 12), (index + i - 4));
        byte[] awal = Arrays.copyOfRange(arr, 0, (index + 12));
        byte[] akhir = Arrays.copyOfRange(arr, (index + i - 4), arr.length);
        int entries = a.length / 4;
//        System.out.println("entries:" + entries);
//        System.out.println(Main.bytesToHex(a));
        System.out.println("===================REPLACE BYTE STCO===========================");
        for (int b = 0; b < entries; b++) {
            int m = b * 4;
            byte[] search = Arrays.copyOfRange(a, m, m + 4);
            int c = Integer.parseInt(Steganography.byteArrayToHex(search), 16);
            String bufHex = Integer.toHexString(c + length);
            byte[] sizeByte = Steganography.hexStringToByteArray(Steganography.panjangData(bufHex));
            for (int d = 0; d < sizeByte.length; d++) {
                a[m] = sizeByte[d];
                m++;
            }

        }
        byte[] temp = concatenateByteArrays(awal, a);
        return concatenateByteArrays(temp, akhir);
//        System.out.println(Main.bytesToHex(a));
//        System.out.println("================================================");
//        return a;
    }

    public static byte[] searchAll(byte[] arr, byte[] sea, int length) {
        int l = 0;
        int k = 0;
        while ((l = Bytes.indexOf(Arrays.copyOfRange(arr, k, arr.length), sea)) > 0) {
            k = k + l;
//            System.out.println("index k:" + k);
            byte[] size = Arrays.copyOfRange(arr, k - 4, k);
            int i = Integer.parseInt(Steganography.byteArrayToHex(size), 16);
//            System.out.println("i:" + i);
            byte[] a = Arrays.copyOfRange(arr, (k + 12), (k + i - 4));
            byte[] awal = Arrays.copyOfRange(arr, 0, (k + 12));
            byte[] akhir = Arrays.copyOfRange(arr, (k + i - 4), arr.length);
            int entries = a.length / 4;
//            System.out.println("entries:" + entries);
//            System.out.println(Main.bytesToHex(a));
//            System.out.println("===================REPLACE BYTE STCO===========================");
            for (int b = 0; b < entries; b++) {
                int m = b * 4;
                byte[] search = Arrays.copyOfRange(a, m, m + 4);
                int c = Integer.parseInt(Steganography.byteArrayToHex(search), 16);
                String bufHex = Integer.toHexString(c + length);
                byte[] sizeByte = Steganography.hexStringToByteArray(Steganography.panjangData(bufHex));
                for (int d = 0; d < sizeByte.length; d++) {
                    a[m] = sizeByte[d];
                    m++;
                }
            }
            byte[] temp = concatenateByteArrays(awal, a);
            arr = concatenateByteArrays(temp, akhir);
//            System.out.println(Main.bytesToHex(a));
//            System.out.println("================================================");
            k += 4;
        }
        return arr;
    }

    public static int embbeded(String in, String out, String message) throws FileNotFoundException, IOException {
        File file = new File(in);
        File fileOut = new File(out);

        int fileSize = (int) file.length();

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] byteArrayIn = new byte[fileSize];

        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.read(byteArrayIn, 0, fileSize);
        dis.close();

        int index = Bytes.indexOf(byteArrayIn, "free".getBytes());
        if (index > 0) {
            byte[] size = Arrays.copyOfRange(byteArrayIn, index - 4, index);
            int i = Integer.parseInt(Steganography.byteArrayToHex(size), 16);
            if (i <= 8) {
                int sizeData = message.length();

                byte[] arr = Steganography.searchAll(byteArrayIn, "stco".getBytes(), sizeData);
                byte[] a = Arrays.copyOf(arr, index - 4);
                byte[] b = Arrays.copyOfRange(arr, (index - 4) + i, byteArrayIn.length);

                String bufHex = Integer.toHexString(sizeData + 4 + 4);
                byte[] sizeByte = Steganography.hexStringToByteArray(Steganography.panjangData(bufHex));

                byte[] k = Steganography.concatenateByteArrays(a, sizeByte);
                byte[] l = Steganography.concatenateByteArrays(k, ("free").getBytes());
                byte[] m = Steganography.concatenateByteArrays(l, message.getBytes());
                byte[] n = Steganography.concatenateByteArrays(m, b);
////
                byteOut.write(n);
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileOut));
                byteOut.writeTo(dos);
                dos.close();
                System.out.println("FINISH");
                return Steganography.BERHASIL;
            } else {
                return Steganography.SUDAH_ADA_DATA;
            }
        } else {
            index = Bytes.indexOf(byteArrayIn, "ftyp".getBytes());

            byte[] size = Arrays.copyOfRange(byteArrayIn, index - 4, index);
            int i = Integer.parseInt(Steganography.byteArrayToHex(size), 16);
            int sizeData = message.length();

            byte[] arr = Steganography.searchAll(byteArrayIn, "stco".getBytes(), sizeData + 8);
            byte[] a = Arrays.copyOf(arr, i);
            byte[] b = Arrays.copyOfRange(arr, i, byteArrayIn.length);

            String bufHex = Integer.toHexString(sizeData + 4 + 4);
            byte[] sizeByte = Steganography.hexStringToByteArray(Steganography.panjangData(bufHex));

            byte[] k = Steganography.concatenateByteArrays(a, sizeByte);
            byte[] l = Steganography.concatenateByteArrays(k, ("free").getBytes());
            byte[] m = Steganography.concatenateByteArrays(l,message.getBytes());
            byte[] n = Steganography.concatenateByteArrays(m, b);
////
            byteOut.write(n);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileOut));
            byteOut.writeTo(dos);
            dos.close();
            System.out.println("FINISH");
            return Steganography.BERHASIL;
        }
    }

    public static int retriveMessage(String in) throws FileNotFoundException, IOException {
        File file = new File(in);

        int fileSize = (int) file.length();

        byte[] byteArrayIn = new byte[fileSize];

        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.read(byteArrayIn, 0, fileSize);
        dis.close();

        int index = Bytes.indexOf(byteArrayIn, "free".getBytes());
        if (index > 0) {
            byte[] size = Arrays.copyOfRange(byteArrayIn, index - 4, index);
            int i = Integer.parseInt(Steganography.byteArrayToHex(size), 16);
            System.out.println("i : " + i);
            if (i > 8) {

                Steganography._message = Arrays.copyOfRange(byteArrayIn, index + 4, (index + i - 4));

//                int sizeData = message.length();

//                byte[] arr = Steganography.searchAll(byteArrayIn, "stco".getBytes(), sizeData);
//                byte[] a = Arrays.copyOf(arr, index - 4);
//                byte[] b = Arrays.copyOfRange(arr, (index - 4) + i, byteArrayIn.length);
//
//                String bufHex = Integer.toHexString(sizeData + 4 + 4);
//                byte[] sizeByte = Steganography.hexStringToByteArray(Steganography.panjangData(bufHex));
//                byte[] k = Steganography.concatenateByteArrays(a, sizeByte);
//                byte[] l = Steganography.concatenateByteArrays(k, ("free" + message).getBytes());
//                byte[] m = Steganography.concatenateByteArrays(l, b);
                System.out.println("FINISH");
                return Steganography.SUDAH_ADA_DATA;
            } else {
                return Steganography.TIDAK_ADA_DATA;
            }
        } else {
            return Steganography.TIDAK_ADA_DATA;
        }
    }

    public static byte[] getMessage() {
        return _message;
    }

}
