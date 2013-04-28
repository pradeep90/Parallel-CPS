package callabletry;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FactorialTest{
    Factorial factorial;
    
    @Before
    public void setUp(){
        factorial = new Factorial();
    }
    
    @After
    public void tearDown(){
    }

    /**
     * Test method for {@link Factorial#fac()}.
     */
    @Test
    public final void testFac(){
        assertEquals(new Long(40320), factorial.fac(new Long(8)));
    }
}
