#RegexReplacer

中文介绍请看这：http://www.cnblogs.com/trytocatch/p/RegexReplacer.html

[Download the latest executable jar.](https://github.com/trytocatch/RegexReplacer/raw/master/executable%20jar/RegexReplacer.jar)

##What it is
This is a desktop application(java swing) to make complex text replacements with regular expression.
####Characteristic:

* It introduced replace functions, you can make some complex replacements with it, if it doesn't meet your needs, you can also write your own function(refer to 'custom function guidance').
* Shows results in real time, you can pick some of them to make replacements, or just view what's matched.
* You can just take the matched content(sometimes it's very useful).

##Replace expression

In the replace expression, you can write replace function inside the plain string(they will be concatenated into a new string)

Its form:
```
$function name(arg1,arg2,arg3)
$function name[alias](arg1,arg2,arg3)
```
ps: the 'alias' is related to function 'Ref' and 'StcRef'.

If you want to write special character, like '$', you should put a escape character '\' ahead of it. 

If you write nothing in the position of a parameter, it will get an empty string rather than 'null'.

The functions in replace expression will be called in order from left to right, you should notice this while you want to use the function 'Ref' or 'StcRef'.

If function A is the parameter of function B, then A will be called before B be called(but the function 'Iif' is an exception)

####Notices: 

* For the functions:Add, Subtract, Multiply, Divide, Mod, Seq, if there is a decimal in their parameters, the result will be a decimal too, otherwise returns a integer(in fact, it's a 'Long'), even for Divide. Empty string and 'null' will be treated as 0, '1.0' and '1.' will be treated as decimal

* If the function 'Seq' be used, and replaced part of the matched content only, the actual replacement may be different from the replacement displayed in the result table(because it is sequence, it depends on the number of replacement)

**Example 1:**
```
"No $Seq(1,1):"
```
It will generates:
```
No 1:
No 2:
No 3:
...
```
**Example 2:**
```
"$Iif($AbsRow(),1,List,No $Seq(1,1):)"
```
It will generates:
```
List
No 1:
No 2:
No 3:
...
```

##Cases:

####1. Contribute to log analysis

**Requirement:** In general, the log contains various informations, sometimes you just need one type of them, and it's difficult to visualize the interested content even with the UE, you may have to view the items one by one with the 'next' button.

**Solution:** Copy the log to the content box, input a regular expression to match the interested content, the result table will display them. Then check the 'return focus' and click one result, the cursor will be located to the right place in content box, you can conveniently get the context. Or you can just pick out all the interested content(put '$(0)' in replace expression box and check 'replacement only', then click the button 'replace all').

####2. capturing group and arithmetic

**Original content:**
```
3*4=?
-6*12=?
9*-5=?
```

**Requirement:** convert to:
```
3*4=12
-6*12=-72
9*-5=-45
```

**Solution:**

*regular expression:*
```
(-?\d+)\*(-?\d+)=\?
```

*replace expression:*
```
$(1)*$(2)=$*($(1),$(2))
```

####3. Sequence

**Original content:**
```
a=34
b=65
c=54
```

**Requirement:** add a sequence number for each line, start with 10, and increment is 10.
```
10. a=34
20. b=65
30. c=54
```

**Solution:** check the regex flag: 'MULTILINE' and input

*regular expression:*
```
^
```

*replace expression:*
```
$Seq(10,10). 
```

####4. Sequence 2

ps: This case is complex and meaningless.Just to show that how complex things it could generate.

**Requirement:** make a multiplication table
```
1 * 1 = 1
1 * 2 = 2	2 * 2 = 4
1 * 3 = 3	2 * 3 = 6	3 * 3 = 9
...
```
**Solution:** write 45 characters as you like in the content box

*regular expression:*
```
.
```
*replace expression:*

```
$Iif($Seq[n](1,1,$Seq[m](1,1,9)),1,,	)$StcRef(n) * $StcRef(m) = $*($StcRef(n),$StcRef(m))$Iif($StcRef(n),$StcRef(m),
,)
```
####5. Case conversion

**Original content:** a snippet from a document
```
permission types: typea,typeb,typec,typed
```
**Requirement:** generate the Java source codes
```
public static final byte TYPEA = 1;
public static final byte TYPEB = 2;
public static final byte TYPEC = 4;
public static final byte TYPED = 8;
```
**Solution:**
*regular expression:*
```
\w+
```
*replace expression:*
```
public static final byte $Upper($(0)) = $Iif[tv]($*[v]($StcRef(tv),2),0,1,$StcRef(v));
```

check the check box 'replacement only', then click the button 'replace all'

####6. Replace strings to assigned strings(complex)

**Original content:** (extract from an examination paper)
```
Miss Carter is a beautiful girl. Her father __ two years ago and her mother made a terrible mistake and __. They began to live a hard life. When she __ middle school, she couldn't go on studying. Her uncle found a __ for her...
```
**Requirement:** put the answers to the right place

answers:
```
1. died
2. left
3. finished
4. job
...
```

**Solution:**

put the content into content box

input a *regular expression*:
```
__
```

input a *replace expression*:
```
\$($Seq(1,1))
```

click 'replace all', get the result(marked as `StrA`): 
```
Miss Carter is a beautiful girl. Her father $(1) two years ago and her mother made a terrible mistake and $(2). They began to live a hard life. When she $(3) middle school, she couldn't go on studying. Her uncle found a $(4) for her...
```

then put the answers into content box

input a *regular expression*:
```
\d+\. (\w+)
```

input a *replace expression*:
```
$(1)
```
check 'replacement only' then click 'replace all', get the result(marked as `StrB`):
```
diedleftfinishedjob
```

change the *replace expression* to: 
```
($(0))
```
check 'replacement only' then click 'replace all', get the result(marked as `StrC`):
```
(died)(left)(finished)(job)
```

Now put the `StrB` into content box

and put the `StrC` into regular expression box

and put the `StrA` into replace expression box

then click 'replace all', you'll get the final result:
```
Miss Carter is a beautiful girl. Her father died two years ago and her mother made a terrible mistake and left. They began to live a hard life. When she finished middle school, she couldn't go on studying. Her uncle found a job for her...
```