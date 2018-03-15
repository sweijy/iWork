package connect.activity.base.compare;

import connect.activity.chat.fragment.bean.SearchBean;

public class SearchCompare extends BaseCompare<SearchBean>  {

    @Override
    public int compare(SearchBean lhs, SearchBean rhs) {
        String lhsStr = lhs.getName();
        String rhsStr = rhs.getName();

        return compareString(lhsStr, rhsStr);
    }

}
