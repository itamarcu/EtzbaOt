package com.example.cukier.shemboard;

public class KeyMap
{
    @SuppressWarnings("SpellCheckingInspection")
    public enum Map
    {
        /**
         * Alphabetical order for the binary numbers.
         */
        אבגדה()
                {
                    @Override
                    String code()
                    {
                        return "אבגד" +
                                "הוזח" +
                                "טיכל" +
                                "מנסע" +
                                "פצקר" +
                                "שת!?" +
                                ",.#-" +
                                "@$ "
                                ;
                    }
                },
        /**
         * Alphabetical order for the binary numbers.
         */
        ABDHP()
                {
                    @Override
                    String code()
                    {
                        return "abcd" +
                                "efgh" +
                                "ijkl" +
                                "mnop" +
                                "qrst" +
                                "uvwx" +
                                "yz#-" +
                                "@$ "
                                ;
                    }
                },
        /**
         * Frequency order for the binary numbers.
         */
        FREQUENCY_ORDER()
                {
                    @Override
                    String code()
                    {
                        return "etao" +
                                "insr" +
                                "hldc" +
                                "umfp" +
                                "gwyb" +
                                "vkxj" +
                                "qz#-" +
                                "@$ "
                                ;
                    }
                },
        /**
         * Frequency order matched with ease of typing each number.
         * <p>
         * 1 touch: 1 2 4 8 16
         * e t a o i
         * <p>
         * 2 touch: 3 6 12 24 5 10 20 9 18 17
         * n s r h l d c u m f
         * <p>
         * 3 touch nice: 7 14 28 21
         * p g [del] w
         * <p>
         * 3 touch ok: 13 26 25 19 11
         * y b v k x
         * <p>
         * 3 touch bad: 22
         * [none]
         * <p>
         * 4 touch: 15 23 27 29 30
         * j z q [switch] [special]
         */
        ETAOI()
                {
                    @Override
                    String code()
                    {
                        return "etna" +
                                "lspo" +
                                "udxr" +
                                "ygji" +
                                "fmkc" +
                                "w#zh" +
                                "vbq-" +
                                "@$ "
                                ;
                    }
                };
        
        abstract String code();
        
        public char decode(int index)
        {
            if (index - 1 < 32)
                return code().charAt(index - 1);
            return '%'; //bug
        }
    }
}
