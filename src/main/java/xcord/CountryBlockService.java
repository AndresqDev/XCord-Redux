package xcord;

import xcord.CountryBlocklist;
import xcord.ReloadableService;
import xcord.XCord;

public class CountryBlockService
implements ReloadableService {
    private CountryBlocklist blocklist = null;

    @Override
    public void reload() throws Exception {
        try {
            if (XCord.getConfig().getKernelLevelIpsetCountryBlockerEnabled()) {
                CountryBlocklist countryBlocklist = new CountryBlocklist("xcord-cty-bl");
                countryBlocklist.blockCountries(XCord.getConfig().getBlockedIsoCodes());
            } else if (this.blocklist != null) {
                this.blocklist.destroy();
                this.blocklist = null;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

