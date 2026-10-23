package br.edu.infnet.gerenciador_senhas_api.service;

import br.edu.infnet.gerenciador_senhas_api.exception.AesEncryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecretKey secretKey;

    public AesEncryptionService(@Value("${api.security.aes.secret}") String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "A chave AES deve ter exatamente 32 bytes (AES-256). " +
                    "Gere uma com: openssl rand -base64 32");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String criptografar(String textoPlano) {
        try {
            byte[] iv = gerarIv();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] ciphertext = cipher.doFinal(textoPlano.getBytes());

            byte[] ivMaisCiphertext = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getEncoder().encodeToString(ivMaisCiphertext);

        } catch (Exception ex) {
            throw new AesEncryptionException("Falha ao criptografar o valor.", ex);
        }
    }

    public String descriptografar(String textoCriptografado) {
        try {
            byte[] ivMaisCiphertext = Base64.getDecoder().decode(textoCriptografado);

            ByteBuffer buffer = ByteBuffer.wrap(ivMaisCiphertext);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            buffer.get(iv);

            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(ciphertext));

        } catch (Exception ex) {
            throw new AesEncryptionException("Falha ao descriptografar o valor.", ex);
        }
    }

    private byte[] gerarIv() {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
