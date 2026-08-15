package com.javalab.mavenclipackaging;

import picocli.CommandLine.IVersionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@code --version}実行時に表示するバージョン文字列を、ビルド時にpom.xmlのバージョンで
 * 置換された{@code app.properties}から読み込んで提供する。
 */
public class AppVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (in != null) {
                properties.load(in);
            }
        }
        return new String[] {"textstat " + properties.getProperty("version", "unknown")};
    }
}
