package com.javalab.jpmsmodule.app;

import com.javalab.jpmsmodule.api.Greeter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@code app}モジュールが{@code greeting-impl}に直接依存せず、{@link ServiceLoader}経由でのみ
 * {@link Greeter}の実装を取得できることを検証する。
 * このテスト自体は{@code com.javalab.jpmsmodule.impl.JapaneseGreeter}を型としてimportしていない
 * (Main.javaと同様にAPIモジュールにのみ依存する)。
 *
 * <p>注意: Maven Surefireはテストクラスをクラスパス(unnamed module)で実行するため、
 * このテストでは{@code java.lang.Module#isExported}によるモジュール境界の検証はできない
 * (unnamed moduleは全パッケージを無条件にexports扱いするため、常にtrueになってしまう)。
 * {@code greeting-impl}パッケージが実際にappから型として参照できないことは、
 * コンパイルエラーとして現れる(README「モジュール境界の確認方法」を参照)。</p>
 */
class GreeterServiceLoaderTest {

    @Test
    void serviceLoader_findsExactlyOneGreeterProvider() {
        List<Greeter> providers = ServiceLoader.load(Greeter.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList();

        assertEquals(1, providers.size());
        assertEquals("こんにちは、太郎さん!", providers.get(0).greet("太郎"));
    }
}
