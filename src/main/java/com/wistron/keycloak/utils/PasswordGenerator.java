package com.wistron.keycloak.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 描述：生成指定位数的随机密码，密码中需要包含大写字母、小写字母、数字和特殊字符中至少三种类型。
 * 方案：用固定长度的数组保存生成的密码，将该数组的索引保存到List中，只要该数组不为空就循环并通过随机数方法取出索引，
 * 下一步是通过随机数方法生成密码种类，最后根据种类通过随机数方法生成具体字符。由于密码种类和长度限制，
 * 使用两个List保存所有种类的Enum类型，其中一个用于生成不同类型字符的字符种类，另一个生成剩下字符的字符种类。
 */

public class PasswordGenerator {
    public static final char[] allowedSpecialCharactors = {
            '`', '~', '@', '#', '$', '%', '^', '&',
            '*', '(', ')', '-', '_', '=', '+', '[',
            '{', '}', ']', '\\', '|', ';', ':', '"',
            '\'', ',', '<', '.', '>', '/', '?'};//密码能包含的特殊字符
    private static final int letterRange = 26;
    private static final int numberRange = 10;
    private static final int spCharactorRange = allowedSpecialCharactors.length;
    private static final Random random = new Random();
    private int passwordLength;//密码的长度
    private int minVariousType;//密码包含字符的最少种类

    public PasswordGenerator(int passwordLength, int minVariousType) {
        if (minVariousType > CharactorType.values().length) minVariousType = CharactorType.values().length;
        if (minVariousType > passwordLength) minVariousType = passwordLength;
        this.passwordLength = passwordLength;
        this.minVariousType = minVariousType;
    }

    public String generateRandomPassword() {
        char[] password = new char[passwordLength];
        List<Integer> pwCharsIndex = new ArrayList();
        for (int i = 0; i < password.length; i++) {
            pwCharsIndex.add(i);
        }
        List<CharactorType> takeTypes = new ArrayList(Arrays.asList(CharactorType.values()));
        List<CharactorType> fixedTypes = Arrays.asList(CharactorType.values());
        int typeCount = 0;
        while (pwCharsIndex.size() > 0) {
            int pwIndex = pwCharsIndex.remove(random.nextInt(pwCharsIndex.size()));//随机填充一位密码
            Character c;
            if (typeCount < minVariousType) {//生成不同种类字符
                c = generateCharacter(takeTypes.remove(random.nextInt(takeTypes.size())));
                typeCount++;
            } else {//随机生成所有种类密码
                c = generateCharacter(fixedTypes.get(random.nextInt(fixedTypes.size())));
            }
            password[pwIndex] = c.charValue();
        }
        return String.valueOf(password);
    }

    private Character generateCharacter(CharactorType type) {
        Character c = null;
        int rand;
        switch (type) {
            case LOWERCASE://随机小写字母
                rand = random.nextInt(letterRange);
                rand += 97;
                c = new Character((char) rand);
                break;
            case UPPERCASE://随机大写字母
                rand = random.nextInt(letterRange);
                rand += 65;
                c = new Character((char) rand);
                break;
            case NUMBER://随机数字
                rand = random.nextInt(numberRange);
                rand += 48;
                c = new Character((char) rand);
                break;
            case SPECIAL_CHARACTOR://随机特殊字符
                rand = random.nextInt(spCharactorRange);
                c = new Character(allowedSpecialCharactors[rand]);
                break;
        }
        return c;
    }

//    public static void main(String[] args) {
//        System.out.println(new PasswordGenerator(10, 3).generateRandomPassword());
//    }
}

enum CharactorType {
    LOWERCASE,
    UPPERCASE,
    NUMBER,
    SPECIAL_CHARACTOR
}
