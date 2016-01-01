package eu.f3rog.blade.compiler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import org.junit.Test;

import javax.tools.JavaFileObject;

import blade.State;
import eu.f3rog.blade.core.BundleWrapper;
import eu.f3rog.blade.core.Weave;

import static eu.f3rog.blade.compiler.util.File.file;
import static eu.f3rog.blade.compiler.util.File.generatedFile;

/**
 * Class {@link StateTest}
 *
 * @author FrantisekGazo
 * @version 2015-11-27
 */
public class StateTest extends BaseTest {

    @Test
    public void invalidField() {
        JavaFileObject input = file("com.example", "MyClass")
                .imports(
                        State.class, "S"
                )
                .body(
                        "public class $T {",
                        "",
                        "   @$S private String mText;",
                        "",
                        "}"
                );

        assertFiles(input)
                .failsToCompile()
                .withErrorContaining(ErrorMsg.Invalid_field_with_annotation.toString(State.class.getSimpleName()));

        input = file("com.example", "MyClass")
                .imports(
                        State.class, "S"
                )
                .body(
                        "public class $T {",
                        "",
                        "   @$S protected String mText;",
                        "",
                        "}"
                );

        assertFiles(input)
                .failsToCompile()
                .withErrorContaining(ErrorMsg.Invalid_field_with_annotation.toString(State.class.getSimpleName()));

        input = file("com.example", "MyClass")
                .imports(
                        State.class, "S"
                )
                .body(
                        "public class $T {",
                        "",
                        "   @$S final String mText;",
                        "",
                        "}"
                );

        assertFiles(input)
                .failsToCompile()
                .withErrorContaining(ErrorMsg.Invalid_field_with_annotation.toString(State.class.getSimpleName()));
    }

    @Test
    public void activity() {
        JavaFileObject input = file("com.example", "MyClass")
                .imports(
                        Activity.class,
                        State.class, "S"
                )
                .body(
                        "public class $T extends Activity {",
                        "",
                        "   @$S String mText;",
                        "   @$S int mNumber;",
                        "",
                        "}"
                );

        JavaFileObject expected = generatedFile("com.example", "MyClass_Helper")
                .imports(
                        input, "I",
                        Bundle.class,
                        BundleWrapper.class,
                        IllegalArgumentException.class, "E",
                        Weave.class
                )
                .body(
                        "public final class $T {",
                        "",
                        "   @Weave(into = \"onSaveInstanceState\", args = {\"android.os.Bundle\"}, statement = \"com.example.$T.saveState(this, $1);\")",
                        "   public static void saveState($I target, Bundle state) {",
                        "       if (state == null) {",
                        "           throw new $E(\"State cannot be null!\");",
                        "       }",
                        "       BundleWrapper bundleWrapper = BundleWrapper.from(state);",
                        "       bundleWrapper.put(\"<Stateful-mText>\", target.mText);",
                        "       bundleWrapper.put(\"<Stateful-mNumber>\", target.mNumber);",
                        "   }",
                        "",
                        "   @Weave(into = \"onCreate\", args = {\"android.os.Bundle\"}, statement = \"com.example.$T.restoreState(this, $1);\")",
                        "   public static void restoreState($I target, Bundle state) {",
                        "       if (state == null) {",
                        "           return;",
                        "       }",
                        "       BundleWrapper bundleWrapper = BundleWrapper.from(state);",
                        "       target.mText = bundleWrapper.get(\"<Stateful-mText>\", target.mText);",
                        "       target.mNumber = bundleWrapper.get(\"<Stateful-mNumber>\", target.mNumber);",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(input)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void view() {
        JavaFileObject input = file("com.example", "MyClass")
                .imports(
                        View.class,
                        Context.class,
                        State.class, "S"
                )
                .body(
                        "public class $T extends View {",
                        "",
                        "   @$S String mText;",
                        "   @$S int mNumber;",
                        "",
                        "   public $T(Context c) {super(c);}",
                        "}"
                );

        JavaFileObject expected = generatedFile("com.example", "MyClass_Helper")
                .imports(
                        input, "I",
                        Bundle.class,
                        BundleWrapper.class,
                        IllegalArgumentException.class, "E",
                        Weave.class
                )
                .body(
                        "public final class $T {",
                        "",
                        "   @Weave(into = \"onSaveInstanceState\", ",
                        "       statement = \"android.os.Bundle bundle = new android.os.Bundle();bundle.putParcelable('PARENT_STATE', super.onSaveInstanceState());com.example.$T.saveState(this, bundle);return bundle;\")",
                        "   public static void saveState($I target, Bundle state) {",
                        "       if (state == null) {",
                        "           throw new $E(\"State cannot be null!\");",
                        "       }",
                        "       BundleWrapper bundleWrapper = BundleWrapper.from(state);",
                        "       bundleWrapper.put(\"<Stateful-mText>\", target.mText);",
                        "       bundleWrapper.put(\"<Stateful-mNumber>\", target.mNumber);",
                        "   }",
                        "",
                        "   @Weave(into = \"onRestoreInstanceState\", args = {\"android.os.Parcelable\"}, ",
                        "       statement = \"if ($1 instanceof android.os.Bundle) {android.os.Bundle bundle = (android.os.Bundle) $1;com.example.$T.restoreState(this, bundle);super.onRestoreInstanceState(bundle.getParcelable('PARENT_STATE'));} else {super.onRestoreInstanceState($1);}return;\")",
                        "   public static void restoreState($I target, Bundle state) {",
                        "       if (state == null) {",
                        "           return;",
                        "       }",
                        "       BundleWrapper bundleWrapper = BundleWrapper.from(state);",
                        "       target.mText = bundleWrapper.get(\"<Stateful-mText>\", target.mText);",
                        "       target.mNumber = bundleWrapper.get(\"<Stateful-mNumber>\", target.mNumber);",
                        "   }",
                        "",
                        "}"
                );

        assertFiles(input)
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

}
