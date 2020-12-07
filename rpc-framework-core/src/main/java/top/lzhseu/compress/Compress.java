package top.lzhseu.compress;

import top.lzhseu.extension.SPI;

/**
 * @author lzh
 * @date 2020/12/5 21:04
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
