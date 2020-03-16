import org.junit.Test;

import java.util.Optional;

import static com.neotys.xray.conf.Constants.SECRET_MANAGED_HOST;

public class test {

    @Test
    public void test()
    {
        String test;
        set("");
        set(null);
        set("test");
        Optional<String> managedHost=Optional.ofNullable(System.getenv(SECRET_MANAGED_HOST)).filter(o->!o.isEmpty());
        if(managedHost.isPresent())
            System.out.println("EXTIST");
        else
            System.out.println("DOES NOT EXISTS");
    }

    public void set(String test)
    {
        Optional<String> tests= Optional.ofNullable(test).filter(o->!o.isEmpty());
        if(tests.isPresent())
            System.out.println("EXTIST");
        else
            System.out.println("DOES NOT EXISTS");
    }
}
