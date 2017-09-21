package org.kie.workbench.common.services.backend.compiler.impl.output;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Created by garfield on 19/09/17.
 */
public class KieSlf4jStdoutLogger implements Logger {
    private static final String ERROR = "[ERROR] ";
    private static final String INFO = "[INFO] ";
    private PrintStream out;

    public KieSlf4jStdoutLogger() {
        this.out = System.out;
    }

    public void error(String msg) {
        this.out.print(ERROR);
        this.out.println(msg);
    }

    public void error(String msg, Throwable t) {
        this.error(msg);
        if(null != t) {
            t.printStackTrace(this.out);
        }

    }

    public String getName() {
        return null;
    }

    public boolean isTraceEnabled() {
        return false;
    }

    public void trace(String msg) {
    }

    public void trace(String format, Object arg) {
    }

    public void trace(String format, Object arg1, Object arg2) {
    }

    public void trace(String format, Object... arguments) {
    }

    public void trace(String msg, Throwable t) {
    }

    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    public void trace(Marker marker, String msg) {
    }

    public void trace(Marker marker, String format, Object arg) {
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
    }

    public void trace(Marker marker, String format, Object... argArray) {
    }

    public void trace(Marker marker, String msg, Throwable t) {
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void debug(String msg) {
    }

    public void debug(String format, Object arg) {
    }

    public void debug(String format, Object arg1, Object arg2) {
    }

    public void debug(String format, Object... arguments) {
    }

    public void debug(String msg, Throwable t) {
    }

    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    public void debug(Marker marker, String msg) {
    }

    public void debug(Marker marker, String format, Object arg) {
    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
    }

    public void debug(Marker marker, String format, Object... arguments) {
    }

    public void debug(Marker marker, String msg, Throwable t) {
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String msg) {
        this.out.print(INFO);
        this.out.println(msg);
    }

    public void info(String format, Object arg) {
        this.out.print(INFO);
        this.out.println(arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        this.out.print(INFO);
        this.out.println(arg1);
        this.out.print(INFO);
        this.out.println(arg2);
    }

    public void info(String format, Object... arguments) {
        this.out.print("[INFO] ");
        this.out.println(arguments);
    }

    public void info(String msg, Throwable t) {
        this.out.print(INFO);
        this.out.println(msg);
    }

    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    public void info(Marker marker, String msg) {
        this.out.print(INFO);
        this.out.println(msg);
    }

    public void info(Marker marker, String format, Object arg) {
        this.out.print(INFO);
        this.out.println(arg);
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        this.out.print(INFO);
        this.out.println(arg1);
    }

    public void info(Marker marker, String format, Object... arguments) {
        this.out.print(INFO);
        this.out.println(arguments);
    }

    public void info(Marker marker, String msg, Throwable t) {
        this.out.print(INFO);
        this.out.println(msg);
    }

    public boolean isWarnEnabled() {
        return false;
    }

    public void warn(String msg) {
    }

    public void warn(String format, Object arg) {
    }

    public void warn(String format, Object... arguments) {
    }

    public void warn(String format, Object arg1, Object arg2) {
    }

    public void warn(String msg, Throwable t) {
    }

    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    public void warn(Marker marker, String msg) {
    }

    public void warn(Marker marker, String format, Object arg) {
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
    }

    public void warn(Marker marker, String format, Object... arguments) {
    }

    public void warn(Marker marker, String msg, Throwable t) {
    }

    public boolean isErrorEnabled() {
        return false;
    }

    public void error(String format, Object arg) {
    }

    public void error(String format, Object arg1, Object arg2) {
    }

    public void error(String format, Object... arguments) {
    }

    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    public void error(Marker marker, String msg) {
    }

    public void error(Marker marker, String format, Object arg) {
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
    }

    public void error(Marker marker, String format, Object... arguments) {
    }

    public void error(Marker marker, String msg, Throwable t) {
    }
}

