#!/usr/bin/env python
# -*- mode: python; coding: utf-8; -*-
# Copyright © 2019 MIT, All rights reserved.
# Released under the Apache License, Version 2.0
# http://www.apache.org/licenses/LICENSE-2.0


"""
Internationalization helper tool for MIT App Inventor. This utility provides two commands, combine and split, to work
with the Java properties and JavaScript files needed to internationalize the App Inventor web editor.

The combine command takes the OdeMessages_default.properties file generated by GWT and the English messages file for
blocklyeditor and combines them into a single .properties file that can be imported into translation services, such as
Google Translate Toolkit.


"""

from __future__ import print_function
import argparse
import os
import os.path
import re


__author__ = 'Evan W. Patton (ewpatton@mit.edu)'


file_path = os.path.realpath(__file__)
js_to_prop = os.path.join(os.path.dirname(file_path), 'js_to_prop.js')
appinventor_dir = os.path.normpath(os.path.join(os.path.dirname(file_path), '../../'))


class CombineAction(argparse.Action):
    def __init__(self, option_strings, dest, nargs=None, **kwargs):
        if nargs is not None:
            raise ValueError('nargs not allowed')
        super(CombineAction, self).__init__(option_strings, dest, **kwargs)

    def __call__(self, parser, namespace, values, option_strings=None):
        pass


blockly_header = """// -*- mode: javascript; js-indent-level: 2; -*-
// Copyright 2018-2019 Massachusetts Institute of Technology. All rights reserved.
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

'use strict';

goog.provide('AI.Blockly.Msg.%(lang)s');
goog.require('Blockly.Msg.%(lang)s');

Blockly.Msg.%(lang)s.switch_language_to_%(lang_name)s = {
  // Switch language to %(lang_name)s.
  category: '',
  helpUrl: '',
  init: function() {
    Blockly.Msg.%(lang)s.switch_blockly_language_to_%(lang)s.init();
"""

blockly_footer = """  }
};
"""


paletteItems = {
    'userInterfaceComponentPallette',
    'layoutComponentPallette',
    'mediaComponentPallette',
    'drawingAndAnimationComponentPallette',
    'mapsComponentPallette',
    'sensorComponentPallette',
    'socialComponentPallette',
    'storageComponentPallette',
    'connectivityComponentPallette',
    'legoMindstormsComponentPallette',
    'experimentalComponentPallette',
    'extensionComponentPallette'
}


def js_stringify(text):
    return "'" + text.replace("''", "'").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r").replace("\\:", ":").replace("\\=", "=") + "'"


def split(args):
    if args.lang is None:
        raise ValueError('No --lang specified for splitting file')
    if args.lang_name is None:
        raise ValueError('No --lang_name specified for splitting file')
    appengine_file = os.path.join(appinventor_dir, 'appengine', 'src', 'com',
                                  'google', 'appinventor', 'client',
                                  'OdeMessages_%s.properties' % args.lang[0])
    blockly_dir = os.path.join(appinventor_dir, 'blocklyeditor', 'src', 'msg', args.lang[0])
    if not os.path.isdir(blockly_dir):
        os.mkdir(blockly_dir)
    blockly_file = os.path.join(blockly_dir, '_messages.js')
    with open(args.source) as source:
        with open(appengine_file, 'w+') as ode_output:
            with open(blockly_file, 'w+') as blockly_output:
                blockly_output.write(blockly_header % {'lang': args.lang[0], 'lang_name': args.lang_name[0]})
                description = None
                for line in source:
                    if len(line) <= 2:
                        pass
                    elif line[0] == '#':
                        if description is not None:
                            description += line
                        else:
                            description = line
                    elif line.startswith('appengine.switchTo') or line.startswith('appengine.SwitchTo'):
                        pass
                    elif line.startswith('appengine.'):
                        if description is not None:
                            ode_output.write(description)
                            description = None
                        line = line[len('appengine.'):]
                        parts = [part.strip() for part in line.split(' = ', 1)]
                        ode_output.write(parts[0])
                        ode_output.write(' = ')
                        if parts[0].endswith('Params') or parts[0].endswith('Properties') or \
                                parts[0].endswith('Methods') or parts[0].endswith('Events') or \
                                (parts[0].endswith('ComponentPallette') and
                                 not parts[0].endswith('HelpStringComponentPallette') and
                                 not parts[0] in paletteItems):
                            parts[1] = ''.join(parts[1].split())
                        ode_output.write(parts[1].replace("'", "''"))
                        ode_output.write('\n\n')
                    else:
                        parts = [part.strip() for part in line[len('blockseditor.'):].split('=', 1)]
                        blockly_output.write('    Blockly.Msg.')
                        blockly_output.write(parts[0])
                        blockly_output.write(' = ')
                        blockly_output.write(js_stringify(parts[1]))
                        blockly_output.write(';\n')
                blockly_output.write(blockly_footer)


def propescape(s):
    return s.replace('\\\\', '\\').replace('\\\'', '\'').replace('\\\"', '\"').replace('\'', '\'\'').replace(':', '\\:').replace('=', '\\=')


def read_block_translations():
    linere = re.compile(r"(Blockly\.Msg\.[A-Z_]+)\s*=\s*?[\"\'\[](.*)[\"\'\]];")
    continuation = re.compile(r'\s*\+?\s*(?:\"|\')?(.*)?(?:\"|\')\s*\+?')
    with open(os.path.join(appinventor_dir, 'blocklyeditor', 'src', 'msg', 'en', '_messages.js')) as js:
        comment = None
        items = []
        full_line = ''
        is_block_comment = False
        is_line_continuation = False
        for line in js:
            line = line.strip()
            if line == '':
                continue
            if line.startswith(r'//'):
                comment = line[3:]
                continue
            if is_block_comment:
                full_line += line
                if line.endswith(r'*/'):
                    comment = full_line
                    is_block_comment = False
                    full_line = ''
                continue
            if line.startswith(r'/*'):
                full_line = line
                is_block_comment = True
                continue
            if line.endswith('{'):
                full_line = ''
                continue
            if line.startswith('+') or line.endswith('+'):
                line = continuation.match(line).group(1)
                is_line_continuation = True
            elif is_line_continuation:
                line = line[1:]
                is_line_continuation = False
            full_line += line
            if full_line.endswith(';'):
                match = linere.match(full_line)
                if match is not None:
                    items.append('blockseditor.%s = %s' % (match.group(1), propescape(match.group(2))))
                    if comment:
                        items.append('# Description: %s' % comment)
                        comment = None
                full_line = ''
        return '\n'.join(items) + '\n'


def combine(args):
    javaprops = os.path.join(appinventor_dir, 'appengine', 'build', 'extra', 'ode',
                             'com.google.appinventor.client.OdeMessages_default.properties')
    blockprops = read_block_translations()  # subprocess.check_output(['node', js_to_prop], text=True, encoding='utf8')
    test = os.path.join(appinventor_dir, 'misc', 'i18n', 'translation_template.properties')
    with open(test, 'w+', encoding='utf8') as out:
        out.write('# Frontend definitions\n')
        with open(javaprops, 'rt', encoding='utf8') as props:
            lastline = ''
            for line in props:
                if lastline.endswith(r'\n') or line.startswith('#') or line.strip() == '':
                    out.write(line)
                else:
                    out.write('appengine.')
                    out.write(line)
        out.write('\n# Blocks editor definitions\n')
        out.write(blockprops)
        out.close()

from xml.etree import ElementTree as et
def tmx_merge(args):
    if args.dest is None:
        raise ValueError('No --dest specified for splitting file')
    tmx_tree = None
    for sfile in args.source_files:
        data = et.parse(sfile).getroot()
        if tmx_tree is None:
            tmx_tree = data
        else:
            for tu in data.iter('tu'):
                test = tu.attrib["tuid"]
                insertion_point = tmx_tree.find("./body/tu/[@tuid='%s']" % test)
                if insertion_point is not None:
                    for child in tu:
                        insertion_point.append(child)
    if tmx_tree is not None:
        with open(args.dest[0], 'w+') as ofile:
            ofile.write(et.tostring(tmx_tree, encoding='utf8').decode('utf8'))
    else:
        raise ValueError('No output')

def parse_args():
    parser = argparse.ArgumentParser(description='App Inventor internationalization toolkit')
    subparsers = parser.add_subparsers()
    parser_combine = subparsers.add_parser('combine')
    parser_combine.set_defaults(func=combine)
    parser_split = subparsers.add_parser('split')
    parser_split.add_argument('--lang', nargs=1, type=str, action='store')
    parser_split.add_argument('--lang_name', nargs=1, type=str, action='store')
    parser_split.add_argument('source')
    parser_split.set_defaults(func=split)
    parser_tmxmerge = subparsers.add_parser('tmx_merge')
    parser_tmxmerge.add_argument('--dest', nargs=1, type=str, action='store')
    parser_tmxmerge.add_argument('source_files', nargs=argparse.REMAINDER)
    parser_tmxmerge.set_defaults(func=tmx_merge)
    args = parser.parse_args()
    if not hasattr(args, 'func'):
        parser.error('One of either combine or split must be specified')
    return args


def main():
    args = parse_args()
    args.func(args)


if __name__ == '__main__':
    main()

