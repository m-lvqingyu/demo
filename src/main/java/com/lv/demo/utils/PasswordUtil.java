package com.lv.demo.utils;

import com.google.common.base.Throwables;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * @author lv
 */
@Slf4j
public class PasswordUtil {

    /**
     * 算法参数
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    /**
     * 迭代次数
     */
    private static final int ITERATIONS = 10000;
    /**
     * 密钥长度
     */
    private static final int KEY_LENGTH = 256;
    /**
     * 盐值长度
     */
    private static final int SALT_LENGTH = 32;

    /**
     * 生成盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @param salt     盐值
     * @return 密码哈希
     */
    public static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("[PasswordUtil]-HashPassword Err! msg:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
    }

    /**
     * 验证密码
     *
     * @param password   用户输入的密码
     * @param salt       存储的盐值
     * @param storedHash 存储的密码哈希
     * @return 是否验证成功
     */
    public static boolean verifyPassword(String password, String salt, String storedHash) {
        String calculatedHash = hashPassword(password, salt);
        return calculatedHash.equals(storedHash);
    }
}
