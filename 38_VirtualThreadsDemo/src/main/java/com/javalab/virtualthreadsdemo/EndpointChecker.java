package com.javalab.virtualthreadsdemo;

import java.util.List;

/** 複数URLへの疎通確認を行う共通インターフェース。実装によって並行処理の方式(スレッドモデル)が異なる。 */
public interface EndpointChecker {

    List<CheckResult> checkAll(List<String> urls);
}
