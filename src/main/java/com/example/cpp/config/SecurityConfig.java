package com.example.cpp.config;

import com.example.cpp.provider.Language;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityConfig {
    private static Map<Language, List<String>> LANGUAGE_SPECIFIC_KEYWORDS = new HashMap<>() {{
        // C/C++ specific
        put(Language.C, Arrays.asList(
                "system", "popen", "exec", "fork", "kill",
                "unistd.h", "sys/types.h", "sys/stat.h"
        ));
        put(Language.CPP, Arrays.asList(
                "system", "popen", "exec", "fork", "kill",
                "unistd.h", "sys/types.h", "sys/stat.h"
        ));

        // Java specific
        put(Language.JAVA, Arrays.asList(
                "Runtime.getRuntime()", "ProcessBuilder",
                "System.exit", "SecurityManager"
        ));

        // Python specific
        put(Language.PYTHON, Arrays.asList(
                "os.", "sys.", "subprocess.", "eval(",
                "__import__", "exec(", "open("
        ));

        // JavaScript specific
        put(Language.JAVASCRIPT, Arrays.asList(
                "process.", "require(", "eval(",
                "Function(", "child_process"
        ));

        // Go specific
        put(Language.GOLANG, Arrays.asList(
                "os.", "exec.", "syscall.",
                "unsafe.", "runtime."
        ));
    }};

    public static boolean isCodeSecure(String sourceCode, Language language) {
        // Kiểm tra các từ khóa chung
        if (!validateSourceCode(sourceCode, language)) {
            return false;
        }

        // Kiểm tra các từ khóa đặc thù theo ngôn ngữ
        List<String> languageKeywords = LANGUAGE_SPECIFIC_KEYWORDS.get(language);
        if (languageKeywords != null) {
            for (String keyword : languageKeywords) {
                if (sourceCode.contains(keyword)) {
                    return false;
                }
            }
        }

        return true;
    }
    private static List<String> getDangerousKeywords() {
        return Arrays.asList(
                "system(", "popen(", "exec", "execve", "Runtime.getRuntime().exec",
                "subprocess.run", "os.system", "__import__", "os.popen",

                "unlink(", "remove(", "rmdir", "delete", "rm ", "shutil.rmtree",
                "os.remove", "os.unlink", "os.rmdir", "File.delete",

                "chmod(", "chown", "setuid", "setgid", "os.chmod",
                "os.chown", "os.setuid", "os.setgid",

                "fork(", "kill(", "signal", "os.kill", "os.fork",
                "signal.signal", "raise", "terminate", "interrupt",

                "/etc/passwd", "/etc/shadow", "/proc/", "authorized_keys",
                ".bash_history", "id_rsa", "known_hosts"

                , "memcpy", "mmap", "munmap",
                "buffer overflow", "heap spray", "shellcode",

                "eval(", "exec(", "compile(", "reactstrap", "assert",
                "__import__", "getattr", "setattr",

                "socket", "bind(", "connect(", "sendto", "recvfrom",
                "urllib.request", "requests.get", "http.client",

                "reverse_shell", "bindshell", "netcat", "nc ",
                "socket.socket", "remote_exec", "telnet",

                "encrypt", "decrypt", "base64", "hashlib", "cryptography",
                "key generator", "random seed"
        );
    }

    private static List<String> getDangerousIncludes() {
        return Arrays.asList(
                // Thư viện hệ thống nguy hiểm
                "<unistd.h>", "<sys/types.h>", "<sys/stat.h>",
                "<windows.h>", "ctypes", "os.", "subprocess",
                "shutil", "win32api", "win32file",

                // Thư viện mạng
                "<netinet/in.h>", "<arpa/inet.h>", "socket",
                "urllib", "requests", "http.client",

                // Thư viện bộ nhớ
                "<memory.h>", "ctypes",
                "mmap", "buffer", "bytearray",

                // Thư viện điều khiển tiến trình
                "<signal.h>", "multiprocessing", "threading",
                "os.fork", "os.kill"
        );
    }

    private static List<String> getDangerousPatterns() {
        return Arrays.asList(
                // Các pattern nhằm bypass
                "char shellcode[]", "\\x90\\x90\\x90",
                "NOP sled", "shellcode injection",
                "\\xeb\\x1e", "\\xff\\xe4", // Jump instructions
                "\\x31\\xc0", // x86 assembly clear register
                "\\xcd\\x80", // Syscall interrupt
                "\\x0f\\x34" // Sysenter instruction
        );
    }

    private static boolean validateSourceCode(String sourceCode, Language language) {
        List<String> dangerousKeywords = getDangerousKeywords();
        List<String> dangerousIncludes = getDangerousIncludes();
        List<String> dangerousPatterns = getDangerousPatterns();

        // Kiểm tra từ khóa nguy hiểm
        for (String keyword : dangerousKeywords) {
            if (sourceCode.contains(keyword)) {
                return false;
            }
        }

        // Kiểm tra include nguy hiểm
        for (String include : dangerousIncludes) {
            if (sourceCode.contains(include)) {
                return false;
            }
        }

        // Kiểm tra pattern nguy hiểm
        for (String pattern : dangerousPatterns) {
            if (sourceCode.contains(pattern)) {
                return false;
            }
        }

        // Kiểm tra các regex pattern phức tạp hơn
        String[] dangerousRegexPatterns = {
                ".*\\b(system|exec|popen|eval)\\s*\\(.*[;`].*\\)",  // Lệnh shell
                ".*Runtime\\.getRuntime\\(\\)\\.exec\\(.*\\)",      // Java runtime exec
                ".*os\\.system\\(.*\\)",                            // Python os system
                ".*\\bchmod\\b.*0777.*"                             // Thay đổi toàn quyền
        };

        for (String regex : dangerousRegexPatterns) {
            if (sourceCode.matches(regex)) {
                return false;
            }
        }

        return true;
    }
}
