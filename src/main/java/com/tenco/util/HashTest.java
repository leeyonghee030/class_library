package com.tenco.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashTest {

    public static void main(String[] args) {

        String password = "admin123";

//        1. 같은 비밀번호를 두번 해시하면 결과가 다르다 (솔트가 매번 다르기 떄문)

        String hash1 = BCrypt.hashpw(password,BCrypt.gensalt(10));
        String hash2 = BCrypt.hashpw(password,BCrypt.gensalt(10));
        System.out.println("해쉬1 : " + hash1);
        System.out.println("해쉬2 : " + hash2);
        System.out.println("두 해시가 같은가" + hash1.equals(hash2));

//        2. 하지만 둘다 원문을 비교해보면 맞다라는 판정을 얻을수있다

        System.out.println(BCrypt.checkpw(password,hash1));
        System.out.println(BCrypt.checkpw(password,hash2));

//        3. 틀린 비밀번호는 거부된다
        System.out.println(BCrypt.checkpw("1234",hash1));

//        4. 일부러 느리다 , 비용을 올리면 두배씩 느려진다
        for (int cost =10; cost <= 12; cost++) {
            long start = System.nanoTime();
            BCrypt.hashpw(password,BCrypt.gensalt(cost));
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.println("비용 : " + cost + " : " + ms + " ms");
        }


    }
}
