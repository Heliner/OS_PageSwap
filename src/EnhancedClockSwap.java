
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EnhancedClockSwap extends BaseSwapPage {
    private static final boolean ACCESSED = true;
    private static final boolean NOT_ACCESSED = false;
    private static final boolean NOT_MODIFIED = false;
    private static final boolean MODIFIED = true;
    List<Page> clockDequeList = new ArrayList<>();

    static final Page FREE_PAGE = new Page(-1);

    int lastReplaceIndex = -1;

    @Override
    public List<Integer> cachedPage() {
        List<Integer> re = new ArrayList<>();
        for (Page page : clockDequeList) {
            re.add(page.pageNum);
        }
        return re;
    }

    @Override
    public void init(int memPagesSize) {
        if (memPagesSize <= 0)
            return;
        this.lastReplaceIndex = -1;
        this.memPagesSize = memPagesSize;
        lostPage = 0;
        serialize = 0;
        clockDequeList.clear();
        for (int i = 0; i < memPagesSize; i++) {
            clockDequeList.add(FREE_PAGE);
        }
    }

    //选择策略
    @Override
    public void selectFIPage(Integer memPageNum) {
        if (memPageNum < 0)
            return;
        /*包含-1*/
        Page pageNew = new Page(memPageNum);
        /*包含当前页面*/
        if (clockDequeList.contains(pageNew)) {

            pageNew = clockDequeList.get(clockDequeList.lastIndexOf(pageNew));
            pageNew.isAccessed = ACCESSED;
            System.out.println("当前分区号存在：" + pageNew.pageNum + "  正在进行访问位修改");

        } else /*保证一定会找到*/ {
            if (clockDequeList.contains(FREE_PAGE)) {
                lostPage++;
                int freeIndex = clockDequeList.indexOf(FREE_PAGE);
                System.out.println("当前页面存在空闲分区 : 分区索引：" + freeIndex);
                clockDequeList.set(freeIndex, pageNew);
                lastReplaceIndex++;
                System.out.println("置换空闲分区:" + freeIndex + "结束");
            } else {
                int replaceIndex = -1;
                do {

                    /*第一步*/
                    for (int i = 0; i < clockDequeList.size(); i++) {
                        Page curPage = clockDequeList.get(i);
                        if (curPage.pageNum != -1 && curPage.isModified == NOT_MODIFIED && curPage.isAccessed == NOT_ACCESSED) {
                            replaceIndex = i;
                            break;
                        }
                    }
                    /*第二步*/
                    if (replaceIndex == -1) {
                        for (int i = 0; i < clockDequeList.size(); i++) {
                            Page curPage = clockDequeList.get(i);
                            if (curPage.pageNum != -1 && curPage.isModified == MODIFIED && curPage.isAccessed == NOT_ACCESSED) {
                                replaceIndex = i;
                                break;
                            }
                            curPage.isAccessed = NOT_ACCESSED;
                        }
                    }
                } while (replaceIndex == -1);
                Page replacePage = clockDequeList.get(replaceIndex);
                System.out.println("开始置换分区 : 分区索引:" + replaceIndex + " 分区值为:" + clockDequeList.get(replaceIndex).pageNum + " 状态：  被访问过:" + replacePage.isAccessed + "  被修改过：" + replacePage.isModified);
                clockDequeList.set(replaceIndex, pageNew);
                System.out.println("置换分区完成");
            }
        }
    }

    @Test
    public void automaticRun() {
        init(3);
        swapOne(7);
        swapOne(0);
        swapOne(1);
        swapOne(2);
        swapOne(0);
        swapOne(3);
        swapOne(0);
        swapOne(4);
        swapOne(2);
        swapOne(3);
        swapOne(0);
        swapOne(3);
        swapOne(2);
        swapOne(1);
        swapOne(2);
        swapOne(0);
        swapOne(1);
        swapOne(7);
        swapOne(0);
        swapOne(1);

        printLostPageAndRatio();
    }


    public void printAllMemPage() {
        System.out.println();
        for (int i = 0; i < clockDequeList.size(); i++) {
            System.out.println("当前页面索引：" + i + " 值为:" + clockDequeList.get(i).pageNum + " 被访问：" + clockDequeList.get(i).isAccessed);
        }
        System.out.println();

    }


    static class Page {
        int pageNum;
        boolean isAccessed = ACCESSED;
        boolean isModified = NOT_MODIFIED;

        public Page(int pageNum) {
            this.pageNum = pageNum;
            this.isAccessed = ACCESSED;
            this.isModified = NOT_MODIFIED;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Page))
                return false;
            return this.pageNum == ((Page) obj).pageNum;
        }
    }
}
