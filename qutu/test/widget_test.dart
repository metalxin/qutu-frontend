import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:qutu_app/app.dart';

void main() {
  testWidgets('Home and create sheet flow', (WidgetTester tester) async {
    await tester.pumpWidget(const QutuApp());

    expect(find.byKey(const ValueKey('home_logo')), findsOneWidget);

    await tester.tap(find.byIcon(Icons.add).first);
    await tester.pumpAndSettle();

    expect(find.text('创建新的行程'), findsOneWidget);

    await tester.tap(find.text('关闭'));
    await tester.pumpAndSettle();

    expect(find.text('创建新的行程'), findsNothing);
  });
}
