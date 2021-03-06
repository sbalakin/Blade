package eu.f3rog.blade.compiler.arg;

import android.app.Fragment;

import org.junit.Test;

import javax.tools.JavaFileObject;

import blade.Arg;
import blade.Blade;
import eu.f3rog.blade.compiler.BaseTest;
import eu.f3rog.blade.compiler.BladeProcessor;
import eu.f3rog.blade.core.BundleWrapper;

import static eu.f3rog.blade.compiler.util.File.file;
import static eu.f3rog.blade.compiler.util.File.generatedFile;

/**
 * Class {@link FragmentFactoryTest}
 *
 * @author FrantisekGazo
 * @version 2015-11-27
 */
public final class FragmentFactoryTest extends BaseTest {

    @Test
    public void none() {
        JavaFileObject input = file("com.example", "SomeFragment")
                .imports(
                        Fragment.class,
                        Blade.class, "B"
                )
                .body(
                        "@$B",
                        "public class $T extends Fragment {}"
                );

        JavaFileObject expected = generatedFile("blade", "F")
                .imports(
                        input, "I",
                        BundleWrapper.class, "BW"
                )
                .body(
                        "public class $T {",
                        "",
                        "   public static $I new$I() {",
                        "       $I fragment = new $I();",
                        "       $BW args = new $BW();",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(input)
                .with(BladeProcessor.Module.ARG)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void one() {
        JavaFileObject input = file("com.example", "SomeFragment")
                .imports(
                        Arg.class, "A",
                        Fragment.class
                )
                .body(
                        "public class $T extends Fragment {",
                        "",
                        "   @$A String mText;",
                        "",
                        "}"
                );

        JavaFileObject expected = generatedFile("blade", "F")
                .imports(
                        input, "I",
                        BundleWrapper.class, "BW",
                        String.class
                )
                .body(
                        "public class $T {",
                        "",
                        "   public static $I new$I(String mText) {",
                        "       $I fragment = new $I();",
                        "       $BW args = new $BW();",
                        "       args.put(\"<Arg-mText>\", mText);",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(input)
                .with(BladeProcessor.Module.ARG)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void more() {
        JavaFileObject input1 = file("com.example", "FirstFragment")
                .imports(
                        Arg.class, "A",
                        Fragment.class
                )
                .body(
                        "public class $T extends Fragment {",
                        "",
                        "   @$A int number;",
                        "",
                        "}"
                );
        JavaFileObject input2 = file("com.example", "SecondFragment")
                .imports(
                        Arg.class, "A",
                        Fragment.class
                )
                .body(
                        "public class $T extends Fragment {",
                        "",
                        "   @$A String text;",
                        "   @$A boolean flag;",
                        "   @$A double number;",
                        "",
                        "}"
                );

        JavaFileObject expected = generatedFile("blade", "F")
                .imports(
                        input1, "I1",
                        input2, "I2",
                        BundleWrapper.class, "BW",
                        String.class
                )
                .body(
                        "public class $T {",
                        "",
                        "   public static $I1 new$I1(int number) {",
                        "       $I1 fragment = new $I1();",
                        "       $BW args = new $BW();",
                        "       args.put(\"<Arg-number>\", number);",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "   public static $I2 new$I2(String text, boolean flag, double number) {",
                        "       $I2 fragment = new $I2();",
                        "       $BW args = new $BW();",
                        "       args.put(\"<Arg-text>\", text);",
                        "       args.put(\"<Arg-flag>\", flag);",
                        "       args.put(\"<Arg-number>\", number);",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(input1, input2)
                .with(BladeProcessor.Module.ARG)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void inheritance() {
        JavaFileObject base = file("com.example", "BaseFragment")
                .imports(
                        Arg.class, "A",
                        Fragment.class
                )
                .body(
                        "public class $T extends Fragment {",
                        "",
                        "   @$A int number;",
                        "",
                        "}"
                );
        JavaFileObject activity = file("com.example", "MyFragment")
                .imports(
                        Arg.class, "A",
                        base, "B"
                )
                .body(
                        "public class $T extends $B {",
                        "",
                        "   @$A String text;",
                        "",
                        "}"
                );

        JavaFileObject expected = generatedFile("blade", "F")
                .imports(
                        base, "B",
                        activity, "A",
                        BundleWrapper.class, "BW",
                        String.class
                )
                .body(
                        "public class $T {",
                        "",
                        "   public static $B new$B(int number) {",
                        "       $B fragment = new $B();",
                        "       $BW args = new $BW();",
                        "       args.put(\"<Arg-number>\", number);",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "   public static $A new$A(int number, String text) {",
                        "       $A fragment = new $A();",
                        "       $BW args = new $BW();",
                        "       args.put(\"<Arg-number>\", number);",
                        "       args.put(\"<Arg-text>\", text);",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(base, activity)
                .with(BladeProcessor.Module.ARG)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void inheritanceFromAbstract() {
        JavaFileObject base = file("com.example", "BaseFragment")
                .imports(
                        Arg.class, "A",
                        Fragment.class
                )
                .body(
                        "public abstract class $T extends Fragment {",
                        "",
                        "   @$A int number;",
                        "",
                        "}"
                );
        JavaFileObject activity = file("com.example", "MyFragment")
                .imports(
                        Arg.class, "A",
                        base, "B"
                )
                .body(
                        "public class $T extends $B {",
                        "",
                        "   @$A String text;",
                        "",
                        "}"
                );

        JavaFileObject expected = generatedFile("blade", "F")
                .imports(
                        activity, "A",
                        BundleWrapper.class, "BW",
                        String.class
                )
                .body(
                        "public class $T {",
                        "",
                        "   public static $A new$A(int number, String text) {",
                        "       $A fragment = new $A();",
                        "       $BW args = new $BW();",
                        "       args.put(\"<Arg-number>\", number);",
                        "       args.put(\"<Arg-text>\", text);",
                        "       fragment.setArguments(args.getBundle());",
                        "       return fragment;",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(base, activity)
                .with(BladeProcessor.Module.ARG)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

}
