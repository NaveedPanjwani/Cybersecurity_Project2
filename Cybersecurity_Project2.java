import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.BadPaddingException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.security.*;
import javax.crypto.*;

public class HW2 {
    static void P1() throws Exception {
        byte[] cipherBMP = Files.readAllBytes(Paths.get("cipher1.bmp"));
        //Created a cipher object using the AES (Rinjdael) algorithm, Cipher Block Chaining as the mode...
        //and ISO10126Padding as the pading scheme.
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
        byte[] key = new byte[] { 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
        //Created a secret key object using an edcoded key and specifying the algorithm
        SecretKeySpec secretkeyspec = new SecretKeySpec(key, "AES");
        byte[] IV = new byte[] { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        //Used an Initialization Vector, since we specified the mode earlier
        IvParameterSpec ivparamspec = new IvParameterSpec(IV);
        //Used the cipher object to decrpyt the ciphertext using the secret key object and the initialization vector
        cipher.init(Cipher.DECRYPT_MODE, secretkeyspec,ivparamspec);
        //.doFinal computes the plaintext using the input of the ciphertext
        byte[] plainBMP = cipher.doFinal(cipherBMP);
        Files.write(Paths.get("plain1.bmp"), plainBMP);
    }

    static void P2() throws Exception {
        byte[] cipher = Files.readAllBytes(Paths.get("cipher2.bin"));
        byte[] modifiedCipher = Files.readAllBytes(Paths.get("cipher2.bin"));
        for(int i=0;i<16;i++){

            modifiedCipher[i] = cipher[i+32];
            modifiedCipher[i+16] = cipher[i+16];
            modifiedCipher[i+32] = cipher[i+0];
        }
        //In order to use a non padded cipher object, we must specify NoPadding as the input
        Cipher cipheralgo = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] key = new byte[] { 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
        SecretKeySpec secretkeyspec = new SecretKeySpec(key, "AES");
        byte[] IV = new byte[] { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        IvParameterSpec ivparamspec = new IvParameterSpec(IV);
        cipheralgo.init(Cipher.DECRYPT_MODE, secretkeyspec,ivparamspec);
        byte[] plain = cipheralgo.doFinal(modifiedCipher);
        Files.write(Paths.get("plain2.txt"), plain);
    }

    static void P3() throws Exception {
        byte[] cipherBMP = Files.readAllBytes(Paths.get("cipher3.bmp"));
        byte[] otherBMP = Files.readAllBytes(Paths.get("plain1.bmp"));
        byte[] modifiedBMP = cipherBMP;
        for(int i=0;i<3000;i++){
            modifiedBMP[i]=otherBMP[i];
        }
        //Performed a transfer of bytes to fully restore a bitmap file
        Files.write(Paths.get("cipher3_modified.bmp"), modifiedBMP);
    }

    static void P4() throws Exception {
        byte[] plainA = Files.readAllBytes(Paths.get("plain4A.txt"));
        byte[] cipherA = Files.readAllBytes(Paths.get("cipher4A.bin"));
        byte[] cipherB = Files.readAllBytes(Paths.get("cipher4B.bin"));
        byte[] key = new byte[cipherA.length];
        byte[] plainB = new byte[cipherB.length];
        int d=0;
        //Used a stream cipher, to XOR a plain text with its corresponding ciphertext in order to obtain a key
        for (int c=0;c<cipherA.length;c++){
            key[c] = (byte) (plainA[c] ^ cipherA[c]);
        }
        //Used a stream cipher, to XOR a cipher text with a key in order to obtain a plain text
        for (int c=0;c<cipherB.length;c++){
            plainB[c] = (byte) (cipherB[c] ^ key[c]);
        }
        Files.write(Paths.get("plain4B.txt"), plainB);
    }

    static void P5() throws Exception {
        //Used my knowledge of characters in ASCII to compute a plain text using brute force
        byte[] cipherBMP = Files.readAllBytes(Paths.get("cipher5.bmp"));
        Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
        byte[] IV = new byte[] { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        IvParameterSpec ivparamspec = new IvParameterSpec(IV);
        byte[] key = new byte[16];
        byte[] plainBMP = new byte [cipherBMP.length];
        for(byte i=0;i<100;i++){
            for (byte j=1;j<13;j++){
                for(byte k=1;k<32;k++){
                    key = new byte[] {   i,  j ,  k,   0,
                            0,   0,    0,   0,
                            0,   0,    0,   0,
                            0,   0,    0,   0
                    };
                    SecretKeySpec secretkeyspec = new SecretKeySpec(key, "AES");
                    cipher.init(Cipher.DECRYPT_MODE, secretkeyspec,ivparamspec);
                    try {
                        plainBMP = cipher.doFinal(cipherBMP);
                    }
                    catch (BadPaddingException e) {
                    }
                    if(plainBMP[0]==66 && plainBMP[1]==77){
                        break;
                    }
                }
                if(plainBMP[0]==66 && plainBMP[1]==77){
                    break;
                }
            }
            if(plainBMP[0]==66 && plainBMP[1]==77){
                break;
            }
        }
        Files.write(Paths.get("plain5.bmp"), plainBMP);
    }
    public static void main(String [] args) {
        try {
            P1();
            P2();
            P3();
            P4();
            P5();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
