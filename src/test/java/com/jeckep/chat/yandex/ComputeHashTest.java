package com.jeckep.chat.yandex;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jeckep on 12.12.16.
 */
public class ComputeHashTest {
    @Test
    public void computeHash(){
        final String data = "p2p-incoming&1234567&300.00&643&2011-07-01T09:00:00.000+04:00&41001XXXXXXXX&false&01234567890ABCDEF01234567890&YM.label.12345";
        final String resultHash = "a2ee4a9195f4a90e893cff4f62eeba0b662321f9";

        final String hex = DigestUtils.sha1Hex(data);

        assertEquals(resultHash, hex);

    }
}
