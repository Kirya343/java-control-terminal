package io.github.kirya343.terminal.runtime;

public interface ApplicationLifecycle {

    void onStarted();

    void onStopped();

    void onFailed(Throwable exception);
}