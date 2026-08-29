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

/**
 * Serviço de criptografia simétrica usando AES-256 no modo GCM (Galois/Counter Mode).
 *
 * <p><strong>Por que GCM e não ECB?</strong><br>
 * O modo ECB é determinístico: o mesmo texto em claro sempre produz o mesmo texto cifrado,
 * o que vaza padrões e é considerado inseguro para dados sensíveis.
 * O modo GCM é <em>Authenticated Encryption with Associated Data</em> (AEAD): gera um IV
 * aleatório de 12 bytes a cada operação e inclui uma tag de autenticação de 128 bits,
 * garantindo confidencialidade <strong>e</strong> integridade dos dados.
 *
 * <p><strong>Formato do valor armazenado no banco:</strong><br>
 * O ciphertext é salvo em Base64 no formato: {@code <IV 12 bytes><ciphertext + GCM tag 16 bytes>},
 * tudo concatenado antes da codificação. O IV é extraído automaticamente na descriptografia.
 *
 * <p><strong>Requisito da chave:</strong><br>
 * A propriedade {@code api.security.aes.secret} deve ter exatamente 32 bytes em Base64
 * (ex: {@code openssl rand -base64 32}).
 */
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

    /**
     * Criptografa um texto em claro usando AES-256-GCM.
     *
     * <p>Um IV aleatório de 12 bytes é gerado para cada chamada. O resultado final é
     * a concatenação {@code IV || ciphertext} codificada em Base64.
     *
     * @param textoPlano texto a ser criptografado; não pode ser nulo
     * @return string Base64 contendo IV + ciphertext cifrado
     * @throws AesEncryptionException se a operação criptográfica falhar
     */
    public String criptografar(String textoPlano) {
        try {
            byte[] iv = gerarIv();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] ciphertext = cipher.doFinal(textoPlano.getBytes());

            // Concatena IV + ciphertext em um único array antes de codificar em Base64
            byte[] ivMaisCiphertext = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getEncoder().encodeToString(ivMaisCiphertext);

        } catch (Exception ex) {
            throw new AesEncryptionException("Falha ao criptografar o valor.", ex);
        }
    }

    /**
     * Descriptografa um valor previamente cifrado por {@link #criptografar(String)}.
     *
     * <p>Os primeiros 12 bytes do payload decodificado são interpretados como o IV;
     * o restante é o ciphertext autenticado pelo GCM.
     *
     * @param textoCriptografado string Base64 contendo IV + ciphertext
     * @return texto em claro original
     * @throws AesEncryptionException se o token for adulterado, expirado ou inválido
     */
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
