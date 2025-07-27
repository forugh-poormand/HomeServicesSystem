package ir.maktab127.homeservicessystem.config;

import java.util.Collection;
import java.util.Collections;

public interface HttpRequestCustomizer {
    default String[] permitAll(){
        return new String[0];
    }

    interface AuthorityBAsed {
        default String[] urls(){
            return new String[0];
        }
        default String[] authorities(){return new String[0];}

    }

    default Collection<AuthorityBAsed> authorizeByAuthority(){
        return Collections.emptyList();
    }
}
