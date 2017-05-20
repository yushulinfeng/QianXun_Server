package com.szdd.qianxun.alipay;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 服务器类-获取付款签名
 */
public class AlipaySignUtils {
    //加密规则
    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKryWhiIIM6cDTDDYNRrSSJCeyF2qTgFzIADdY9V+NS4Q+XfujDQs9YvBKN6pqSH0wUO7s5lWEUKY5tZgOsJ08yVqNsnXtnjIdLoOv+qg25D3mfqN/Zy81s3fSS+gKE7L95LWhMviXQOnc+Ktt/KCifpVpJshUIEiT9d0w28NpwtAgMBAAECgYByvlYHXWhYBps7/gkCzDi8zE8F+TZAqnPIAYppedWaQ00gAXpixdPQkqLUcMFmct0L6igVViVXwk91uTA1k9Ubxnb1yWiV3KDH0NW+IQIZTS4Mv5Krl0dn54E9nnrz33YkJ2fhzlGLxDqghxc4ROl6n4A8xD78XtuJHgkbZ+bKKQJBAN8MqqbTN28TJJqXcPkfh0upR0d+Coa8qMyfWAjXVbyXqr0v4p2WBjzMkpJo8tSMoQ2w6SMhDIMnveOP1O/+GHcCQQDEM0Atf7tscRMQfXgM+e1gDfZvTvnACa6YNI/Z0gSKa3CjtVmtjP5ap6XH5jPW+W0/GPGwrW0p/ulwkORot717AkBopQ2vJOsd/pIuwRwslOEp2ypo3TZdDkf3cE6iHzzHNTTSe5PLMSaOAcBGVlyO4cdxn+1RIOE46z1IdSOG0ORzAkAHDaCdAh6Gw3nf+eK69LEn5tiZH8mysaZpKlqRhg9LupkOCZLzvkHWmHhJO2lZrRXUUt09cL6f594UgeD0MsbJAkEAikLOFFlMYsvTHYHyFS5EWAgrFvH1VsdAKtti3xebpeZpUic6R2XO79bI5FoIHePVNaped7QMSKhDVGwMgSrpjQ==";

    public static String getAlipaySign(String content) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(RSA_PRIVATE));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";//错误标志
    }

}
