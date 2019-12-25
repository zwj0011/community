package xyz.oldz.community.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author lz
 */
@Slf4j
@Component
public class SensitiveFilter {

  // 替换符
  private static final String REPLACEMENT = "**";

  // 根节点
  private TrieNode rootNode = new TrieNode();

  @PostConstruct
  public void init() {
    try (InputStream inputStream = this.getClass().getClassLoader()
        .getResourceAsStream("sensitive-words.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String keyword;
      while ((keyword = reader.readLine()) != null) {
        // 添加到前缀树
        this.addKeyword(keyword);
      }

    } catch (Exception e) {
      log.error("加载敏感词文件失败：" + e.getMessage());
    }
  }

  public void addKeyword(String keyword) {
    TrieNode tempNode = rootNode;
    for (int i = 0; i < keyword.length(); i++) {
      char c = keyword.charAt(i);
      //先获取子节点
      TrieNode subNode = tempNode.getSubNode(c);
      //如果子节点不存在，新增这个子节点，并添加到当前根节点的子节点中
      if (subNode == null) {
        subNode = new TrieNode();
        tempNode.addSubNode(c, subNode);
      }
      //进入下一层
      tempNode = subNode;
      //遍历到字符串结尾，设置结束标志
      if (i == keyword.length() - 1) {
        subNode.setKeyWordEnd(true);
      }
    }
  }

  /**
   * 过滤敏感词
   *
   * @param text 待过滤文本
   * @return 过滤后的文本
   */
  public String filter(String text) {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    // 指针1
    TrieNode tempNode = rootNode;
    // 指针2，全局走一遍
    int begin = 0;
    // 指针3，在begin基础上进行抖动，通过begin~position来截取敏感词
    int position = 0;

    StringBuilder stringBuilder = new StringBuilder();
    // 一开始使用begin作为循环结束条件，但在测试用例：敏感词String，传入文本Str，情况下会进行无意义的多次循环
    // while (begin<text.length()){
    // 使用position作为结束判定条件，循环结束时，begin后面的字符可能会没加进去，在返回前需要加上
    while (position < text.length()) {
      char c = text.charAt(position);
      // 跳过特殊符号
      if (isSymbol(c)) {
        // 若指针1处于根节点，将此符号计入结果，让指针2走向下走一步
        if (tempNode == rootNode) {
          stringBuilder.append(c);
          begin++;
        }
        position++;
        continue;
      }
      // 获取子节点
      tempNode = tempNode.getSubNode(c);
      // 节点为null，则没有敏感词开头的char
      if (tempNode == null) {
        stringBuilder.append(c);
        // begin指针向后移动
        begin = begin + 1;
        position = begin;
        // tempNode回到根节点
        tempNode = rootNode;
      }
      // 如果当前节点为敏感词结尾，替换text begin~position位置的字符，此处采用往StringBuilder添加**方式
      else if (tempNode.isKeyWordEnd) {
        stringBuilder.append(REPLACEMENT);
        position = position + 1;
        begin = position;
        tempNode = rootNode;
      } else {
        // 当前节点不为空，且不是敏感词结尾，表示匹配到敏感词的一部分，移动position指针继续标记
        position++;
      }
    }
    // 将最后一批字符计入结果
    stringBuilder.append(text.substring(begin));
    return stringBuilder.toString();
  }

  /**
   * 判断是否为特殊符号
   */
  private boolean isSymbol(Character c) {
    //0x2E80~0x9FFF 东亚文字，包含中文，日文等
    return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
  }

  // 前缀树
  private class TrieNode {

    // 关键词结束标志
    private boolean isKeyWordEnd = false;

    // 子节点(key 下级节点字符，value 下级字符)
    private Map<Character, TrieNode> subNodes = new HashMap<>();

    public boolean isKeyWordEnd() {
      return isKeyWordEnd;
    }

    private void setKeyWordEnd(boolean keyWordEnd) {
      isKeyWordEnd = keyWordEnd;
    }

    //添加子节点
    private void addSubNode(Character c, TrieNode node) {
      subNodes.put(c, node);
    }

    //获取子节点
    private TrieNode getSubNode(Character c) {
      return subNodes.get(c);
    }
  }
}
