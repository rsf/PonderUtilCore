/* This was:
 * Id: SHA1.java,v 1.6 2001/06/25 15:39:55 gelderen Exp 
 * http://anoncvs.cryptix.org/cvs.php/projects/jce/src/cryptix.jce.provider.md/SHA1.java
 * Copyright (C) 1995-2000 The Cryptix Foundation Limited.
 * All rights reserved.
 *
 * Use, modification, copying and distribution of this software is subject to
 * the terms and conditions of the Cryptix General Licence. You should have
 * received a copy of the Cryptix General Licence along with this library;
 * if not, you can download a copy from http://www.cryptix.org/
 * Cryptix General License
 * Copyright (C) 1995-2001 The Cryptix Foundation Limited. All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:

 * Redistributions of source code must retain the copyright notice,
 * this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the
 * distribution.

 * THIS SOFTWARE IS PROVIDED BY THE CRYPTIX FOUNDATION LIMITED AND
 * CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CRYPTIX FOUNDATION LIMITED OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package uk.org.ponder.hashutil;


/**
 * SHA-1 message digest algorithm.
 *
 * @version Revision: 1.6
 * @author  Jeroen C. van Gelderen
 * @author  Antranig Basman.
 */
public final class SHA1 extends SHA implements Cloneable {

// Constructors
//...........................................................................

  public SHA1() {
    super();
    }
  
  private SHA1(SHA1 src) {
    super(src);
    }
  
  public Object clone() {
    return new SHA1(this);
    }
  
// Concreteness
//...........................................................................

  protected void expand(int[] W) {
    // expand the block to 80 words, according to the SHA1 spec
    for(int i = 16; i < 80; i++) {
      int j = W[i-16] ^ W[i-14] ^ W[i-8] ^ W[i-3];
      W[i] = (j << 1) | (j >>> -1);
      }
    }
  }
