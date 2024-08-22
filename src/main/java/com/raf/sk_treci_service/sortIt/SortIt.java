package com.raf.sk_treci_service.sortIt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortIt {
    private String sortField; // polje po kojem se sortira, npr. "datum", "tip", itd.
    private SortDirection sortDirection; // smer sortiranja, npr. ASC ili DESC

    // Konstruktori, getteri i setteri

    public enum SortDirection {
        ASC, // Za rastući redosled
        DESC // Za opadajući redosled
    }
}
