package connect.activity.base.compare;

import connect.database.green.bean.OrganizerEntity;

/**
 * Created by Administrator on 2018/3/16 0016.
 */

public class DepartmentCompare extends BaseCompare<OrganizerEntity> {

    @Override
    public int compare(OrganizerEntity lhs, OrganizerEntity rhs) {
        String lhsStr = lhs.getName();
        String rhsStr = rhs.getName();

        return compareString(lhsStr, rhsStr);
    }
}
